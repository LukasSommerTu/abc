/* Abc - The AspectBench Compiler
 * Copyright (C) 2004 Aske Simon Christensen
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL; 
 * if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package abc.weaving.aspectinfo;

import polyglot.util.Position;

import java.util.*;

/** A declare precedence declaration 
 *  @author Aske Simon Christensen
 */
public class DeclarePrecedence extends InAspect {
    private List/*<ClassnamePattern>*/ patterns;

    /** Create a new <code>declare precedence</code>.
     *  @param patterns a list of {@link abc.weaving.aspectinfo.ClassnamePattern} objects.
     */
    public DeclarePrecedence(List patterns, Aspect aspct, Position pos) {
	super(aspct, pos);
	this.patterns = patterns;
    }

    /** Get the patterns matching the aspects to be ordered.
     *  @return a list of {@link abc.weaving.aspectinfo.ClassnamePattern} objects.
     */
    public List/*<ClassnamePattern>*/ getPatterns() {
	return patterns;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("declare precedence: ");
	Iterator cpi = patterns.iterator();
	while (cpi.hasNext()) {
	    ClassnamePattern cp = (ClassnamePattern)cpi.next();
	    sb.append(cp);
	    if (cpi.hasNext()) {
		sb.append(", ");
	    }
	}
	sb.append(";");
	return sb.toString();
    }
}
