/*
 * Copyright (c) 2018 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.itemstats;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

public class ItemStatOverlay extends Overlay
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private ItemStatChanges statChanges;

	@Inject
	private ItemStatBonuses statBonuses;

	@Inject
	private ItemStatConfig config;

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.isMenuOpen() || (!config.relative() && !config.absolute() && !config.theoretical()))
		{
			return null;
		}

		final MenuEntry[] menu = client.getMenuEntries();
		final int menuSize = menu.length;

		if (menuSize <= 0)
		{
			return null;
		}

		final MenuEntry entry = menu[menuSize - 1];
		final int group = WidgetInfo.TO_GROUP(entry.getParam1());
		final int child = WidgetInfo.TO_CHILD(entry.getParam1());
		final Widget widget = client.getWidget(group, child);

		if (widget == null || (group != WidgetInfo.INVENTORY.getGroupId() &&
			group != WidgetInfo.EQUIPMENT.getGroupId() &&
			group != WidgetInfo.EQUIPMENT_INVENTORY_ITEMS_CONTAINER.getGroupId()))
		{
			return null;
		}

		int itemId = entry.getIdentifier();

		if (group == WidgetInfo.EQUIPMENT.getGroupId())
		{
			final Widget widgetItem = widget.getChild(1);
			if (widgetItem != null)
			{
				itemId = widgetItem.getItemId();
			}
		}
		else if (group == WidgetInfo.EQUIPMENT_INVENTORY_ITEMS_CONTAINER.getGroupId())
		{
			final Widget widgetItem = widget.getChild(entry.getParam0());
			if (widgetItem != null)
			{
				itemId = widgetItem.getItemId();
			}
		}

		final Effect change = statChanges.get(itemId);
		if (change != null)
		{
			final StringBuilder b = new StringBuilder();
			final StatsChanges statsChanges = change.calculate(client);

			for (final StatChange c : statsChanges.getStatChanges())
			{
				b.append(buildStatChangeString(c));
			}

			tooltipManager.add(new Tooltip(b.toString()));
			return null;
		}

		final ItemComposition item = itemManager.getItemComposition(itemId);
		if (item != null)
		{
			final ItemStatBonuses.ItemStats stats = statBonuses.getBonus(item);

			if (stats != null)
			{
				tooltipManager.add(new Tooltip(buildStatBonusString(stats)));
			}
		}

		return null;
	}

	private String getChangeString(final String label, final double value, final boolean inverse)
	{
		final Color plus = Positivity.getColor(config, Positivity.BETTER_UNCAPPED);
		final Color minus = Positivity.getColor(config, Positivity.WORSE);

		if (value == 0)
		{
			return "";
		}

		final Color color;

		if (inverse)
		{
			color = value > 0 ? minus : plus;
		}
		else
		{
			color = value > 0 ? plus : minus;
		}

		final String valueString = (int)value == value ? String.valueOf((int)value) : String.valueOf(value);
		final String symbol = value > 0 ? "+" : "";
		return label + ": " + ColorUtil.wrapWithColorTag(symbol + valueString, color) + "</br>";
	}

	private String buildStatBonusString(ItemStatBonuses.ItemStats s)
	{
		StringBuilder b = new StringBuilder();
		b.append(getChangeString("Weight", s.getWeight(), true));

		final ItemStatBonuses.ItemEquipmentStats e = s.getEquipment();
		if (s.isEquipable() && e != null)
		{
			b.append(getChangeString("Stab Attack", e.getAstab(), false));
			b.append(getChangeString("Slash Attack", e.getAslash(), false));
			b.append(getChangeString("Crush Attack", e.getAcrush(), false));
			b.append(getChangeString("Magic Attack", e.getAmagic(), false));
			b.append(getChangeString("Range Attack", e.getArange(), false));

			b.append(getChangeString("Stab Defense", e.getDstab(), false));
			b.append(getChangeString("Slash Defense", e.getDslash(), false));
			b.append(getChangeString("Crush Defense", e.getDcrush(), false));
			b.append(getChangeString("Magic Defense", e.getDmagic(), false));
			b.append(getChangeString("Range Defense", e.getDrange(), false));

			b.append(getChangeString("Melee Strength", e.getStr(), false));
			b.append(getChangeString("Range Strength", e.getRstr(), false));
			b.append(getChangeString("Magic Damage", e.getMdmg(), false));
			b.append(getChangeString("Prayer", e.getPrayer(), false));
			b.append(getChangeString("Attack Speed", e.getAspeed(), false));
		}

		return b.toString();
	}

	private String buildStatChangeString(StatChange c)
	{
		StringBuilder b = new StringBuilder();
		b.append(ColorUtil.colorTag(Positivity.getColor(config, c.getPositivity())));

		if (config.relative())
		{
			b.append(c.getRelative());
		}

		if (config.theoretical())
		{
			if (config.relative())
			{
				b.append("/");
			}
			b.append(c.getTheoretical());
		}

		if (config.absolute() && (config.relative() || config.theoretical()))
		{
			b.append(" (");
		}
		if (config.absolute())
		{
			b.append(c.getAbsolute());
		}

		if (config.absolute() && (config.relative() || config.theoretical()))
		{
			b.append(")");
		}
		b.append(" ").append(c.getStat().getName());
		b.append("</br>");

		return b.toString();
	}
}
