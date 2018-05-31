/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
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
package net.runelite.api;

import java.awt.Color;
import java.util.List;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;

/**
 * Represents the model of an object.
 */
public interface Model extends Renderable
{
	/**
	 * Gets the amount of vertices in the model.
	 */
	int getVerticesCount();

	/**
	 * Gets the x position of the vertices in the model.
	 */
	int[] getVerticesX();

	/**
	 * Gets the y positions of the vertices in the model.
	 */
	int[] getVerticesY();

	/**
	 * Gets the z positions of the vertices in the model.
	 */
	int[] getVerticesZ();

	/**
	 * Gets the amount of triangles in the model.
	 */
	int getTrianglesCount();

	/**
	 * Gets the first vertex index of the triangles in the model.
	 */
	int[] getTrianglesX();

	/**
	 * Gets the second vertex index of the triangles in the model.
	 */
	int[] getTrianglesY();

	/**
	 * Gets the third vertex index of the triangles in the model.
	 */
	int[] getTrianglesZ();

	/**
	 * Gets the transparency values of the triangles in the model, or null if
	 * no parts of the model is transparent. 254 and 255 is fully transparent
	 * and 0 is fully opaque. Note that negative values needs to be transformed
	 * by casting to a type larger than 8 bits and removing any bits except 0xFF.
	 */
	byte[] getTriangleTransparencies();

	/**
	 * Gets a list of all vertices of the model.
	 *
	 * @return the vertices
	 */
	List<Vertex> getVertices();

	/**
	 * Gets a list of all triangles of the model.
	 *
	 * @return the triangle
	 */
	List<Triangle> getTriangles();

	/**
	 * Draw an outline around the model.
	 *
	 * @param localX The local x position of the model
	 * @param localY The local y position of the model
	 * @param localZ The local z position of the model
	 * @param orientation The orientation of the model
	 * @param outlineWidth The width of the outline
	 * @param innerColor The color of the pixels of the outline closest to the model
	 * @param outerColor The color of the pixels of the outline furthest away of the model
	 */
	void drawOutline(int localX, int localY, int localZ, int orientation,
		int outlineWidth, Color innerColor, Color outerColor);
}
