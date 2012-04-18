/**********************************************************************
Copyright (c) 2007 Thomas Marti and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.datanucleus.store.mapped.mapping;

import java.awt.geom.Arc2D;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.NucleusContext;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.ExecutionContext;
import org.datanucleus.store.mapped.DatastoreContainerObject;
import org.datanucleus.store.mapped.MappedStoreManager;

/**
 * Mapping for java.awt.geom.Arc2D.Float, maps the x, y, width, height, start and extent values to float-precision
 * datastore fields.
 */
public class Arc2dFloatMapping extends SingleFieldMultiMapping
{
    /* (non-Javadoc)
     * @see org.datanucleus.store.mapping.JavaTypeMapping#initialize(AbstractMemberMetaData, DatastoreContainerObject, ClassLoaderResolver)
     */
    public void initialize(AbstractMemberMetaData fmd, DatastoreContainerObject container, ClassLoaderResolver clr)
    {
        super.initialize(fmd, container, clr);
        addDatastoreFields();
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.mapped.mapping.JavaTypeMapping#initialize(MappedStoreManager, java.lang.String)
     */
    public void initialize(MappedStoreManager storeMgr, String type)
    {
        super.initialize(storeMgr, type);
        addDatastoreFields();
    }

    protected void addDatastoreFields()
    {
        addDatastoreField(ClassNameConstants.INT);   // Type
        addDatastoreField(ClassNameConstants.FLOAT); // X
        addDatastoreField(ClassNameConstants.FLOAT); // Y
        addDatastoreField(ClassNameConstants.FLOAT); // Width
        addDatastoreField(ClassNameConstants.FLOAT); // Height
        addDatastoreField(ClassNameConstants.FLOAT); // Start
        addDatastoreField(ClassNameConstants.FLOAT); // Extent
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.mapping.JavaTypeMapping#getJavaType()
     */
    public Class getJavaType()
    {
        return Arc2D.Float.class;
    }

    /**
     * Method to return the value to be stored in the specified datastore index given the overall
     * value for this java type.
     * @param index The datastore index
     * @param value The overall value for this java type
     * @return The value for this datastore index
     */
    public Object getValueForDatastoreMapping(NucleusContext nucleusCtx, int index, Object value)
    {
        Arc2D.Float arc = (Arc2D.Float)value;
        if (index == 0)
        {
            return arc.getArcType();
        }
        else if (index == 1)
        {
            return arc.getX();
        }
        else if (index == 2)
        {
            return arc.getY();
        }
        else if (index == 3)
        {
            return arc.getWidth();
        }
        else if (index == 4)
        {
            return arc.getHeight();
        }
        else if (index == 5)
        {
            return arc.getAngleStart();
        }
        else if (index == 6)
        {
            return arc.getAngleExtent();
        }
        throw new IndexOutOfBoundsException();
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.mapping.JavaTypeMapping#setObject(org.datanucleus.ObjectManager, java.lang.Object, int[], java.lang.Object)
     */
    public void setObject(ExecutionContext ec, Object preparedStatement, int[] exprIndex, Object value)
    {
    	Arc2D.Float arc = (Arc2D.Float)value;
        if (arc == null)
        {
    		for (int i = 0; i < exprIndex.length; i++) 
    		{
    			getDatastoreMapping(i).setObject(preparedStatement, exprIndex[i], null);					
			}
        }
        else
        {
            getDatastoreMapping(0).setInt(preparedStatement,exprIndex[0],arc.getArcType());
            getDatastoreMapping(1).setFloat(preparedStatement,exprIndex[1],arc.x);
            getDatastoreMapping(2).setFloat(preparedStatement,exprIndex[2],arc.y);
            getDatastoreMapping(3).setFloat(preparedStatement,exprIndex[3],arc.width);
            getDatastoreMapping(4).setFloat(preparedStatement,exprIndex[4],arc.height);
            getDatastoreMapping(5).setFloat(preparedStatement,exprIndex[5],arc.start);
            getDatastoreMapping(6).setFloat(preparedStatement,exprIndex[6],arc.extent);
        }
    }
    
    /* (non-Javadoc)
     * @see org.datanucleus.store.mapping.JavaTypeMapping#getObject(org.datanucleus.ObjectManager, java.lang.Object, int[])
     */
    public Object getObject(ExecutionContext ec, Object resultSet, int[] exprIndex)
    {
        // Check for null entries
        if (getDatastoreMapping(0).getObject(resultSet, exprIndex[0]) == null)
        {
            return null;
        }
        
        int type = getDatastoreMapping(0).getInt(resultSet,exprIndex[0]); 
        float x = getDatastoreMapping(1).getFloat(resultSet,exprIndex[1]); 
        float y = getDatastoreMapping(2).getFloat(resultSet,exprIndex[2]); 
        float width  = getDatastoreMapping(3).getFloat(resultSet,exprIndex[3]); 
        float height = getDatastoreMapping(4).getFloat(resultSet,exprIndex[4]);
        float start  = getDatastoreMapping(5).getFloat(resultSet,exprIndex[5]); 
        float extent = getDatastoreMapping(6).getFloat(resultSet,exprIndex[6]);
        return new Arc2D.Float(x, y, width, height, start, extent, type);
    }
}