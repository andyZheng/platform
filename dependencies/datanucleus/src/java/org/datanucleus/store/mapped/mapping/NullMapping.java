/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved. 
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

import org.datanucleus.store.ExecutionContext;
import org.datanucleus.store.mapped.MappedStoreManager;

/**
 * Simple mapping for a null literal. Only used when the type is not determined
 */
public class NullMapping extends SingleFieldMapping
{
    public NullMapping(MappedStoreManager storeMgr)
    {
        initialize(storeMgr, null);
    }

    public Class getJavaType()
    {
        return null;
    }

    public Object getObject(ExecutionContext ec, Object resultSet, int[] exprIndex)
    {
        return null;
    }
    
    public void setObject(ExecutionContext ec, Object preparedStatement, int[] exprIndex, Object value)
    {
    }
}