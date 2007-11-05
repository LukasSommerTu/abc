/* abc - The AspectBench Compiler
 * Copyright (C) 2007 Eric Bodden
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package abc.tm.weaving.weaver.tmanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.Context;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.SootField;
import soot.Type;
import soot.jimple.spark.ondemand.AllocAndContext;
import soot.jimple.spark.ondemand.AllocAndContextSet;
import soot.jimple.spark.ondemand.DemandCSPointsTo;
import soot.jimple.spark.ondemand.WrappedPointsToSet;

/**
 * A special version of {@link DemandCSPointsTo} that first queries the on demand analysis with call graph refinement and in case it fails
 * then tries again without refinement. 
 *
 * @author Eric Bodden
 */
public class CustomizedDemandCSPointsTo implements PointsToAnalysis {
    
    protected final DemandCSPointsTo delegate;

    public CustomizedDemandCSPointsTo(DemandCSPointsTo delegate) {
        this.delegate = delegate;
    }

    public PointsToSet reachingObjects(Local l) {
        delegate.setRefineCallGraph(true);
        PointsToSet reachingObjects = delegate.reachingObjects(l);
        if(reachingObjects instanceof WrappedPointsToSet) {
            //had to abort, returning Spark's points-to set;
            //try again without call-graph refinement
            delegate.setRefineCallGraph(false);
            reachingObjects = delegate.reachingObjects(l);
        }
        if(reachingObjects instanceof AllocAndContextSet) {
			AllocAndContextSet pts = (AllocAndContextSet) reachingObjects;
			Set<AllocAndContext> toRemove = new HashSet<AllocAndContext>();
			for (Iterator<AllocAndContext> iter = pts.iterator(); iter.hasNext();) {
				AllocAndContext allocNodeAndContext = iter.next();
				Type allocType = allocNodeAndContext.alloc.getType();
				if(allocNodeAndContext.context.isEmpty() &&
						allocType.toString().contains("Empty")) {
					toRemove.add(allocNodeAndContext);
				}
			}
        	pts.removeAll(toRemove);
        	if(once) {
        		System.out.println("XXXXXX removed: "+toRemove);
        		once = false;
        	}
        }
        
        return reachingObjects;
    }
    
    private boolean once = true;

    public PointsToSet reachingObjects(Context c, Local l, SootField f) {
        return delegate.reachingObjects(c, l, f);
    }

    public PointsToSet reachingObjects(Context c, Local l) {
        return delegate.reachingObjects(c, l);
    }

    public PointsToSet reachingObjects(Local l, SootField f) {
        return delegate.reachingObjects(l, f);
    }

    public PointsToSet reachingObjects(PointsToSet s, SootField f) {
        return delegate.reachingObjects(s, f);
    }

    public PointsToSet reachingObjects(SootField f) {
        return delegate.reachingObjects(f);
    }

    public PointsToSet reachingObjectsOfArrayElement(PointsToSet s) {
        return delegate.reachingObjectsOfArrayElement(s);
    }
}
