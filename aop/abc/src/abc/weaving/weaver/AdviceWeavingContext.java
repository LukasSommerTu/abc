/* abc - The AspectBench Compiler
 * Copyright (C) 2004 Ganesh Sittampalam
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

package abc.weaving.weaver;

import java.util.Vector;
import java.util.Hashtable;
import soot.Local;

/** Keep track of the "weaving context" for
 *  a concrete advice decl
 *  @author Ganesh Sittampalam
 */

public class AdviceWeavingContext extends WeavingContext {
    public Vector/*<Value>*/ arglist;
    public Local aspectinstance;


    // locals get stored in the residue itself
    
    // insert reflective stuff here too

    public AdviceWeavingContext(Vector arglist) {
	this.arglist=arglist;
	this.aspectinstance=null;
    }
}
