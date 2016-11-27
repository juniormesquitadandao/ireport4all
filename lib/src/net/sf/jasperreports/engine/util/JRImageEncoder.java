/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine.util;

import java.awt.Image;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.type.ImageTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRImageEncoder.java 7199 2014-08-27 13:58:10Z teodord $
 */
public interface JRImageEncoder
{


	/**
	 *
	 */
	public byte[] encode(Image image, ImageTypeEnum imageType) throws JRException;

}