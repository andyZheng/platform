/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.store.types;

import org.datanucleus.util.Localiser;

/**
 * Interface for an object that can be converted to/from a String.
 * Used for datastores that allow persistence of Strings but not of a native Java type.
 */
public interface ObjectStringConverter
{
    /** Localiser for messages */
    static final Localiser LOCALISER = Localiser.getInstance(
        "org.datanucleus.Localisation", org.datanucleus.ClassConstants.NUCLEUS_CONTEXT_LOADER);

    /**
     * Method to convert from the object to its String form
     * @param obj The object
     * @return String form
     */
    public String toString(Object obj);

    /**
     * Method to convert from String back to the object.
     * @param str The string
     * @return The object
     */
    public Object toObject(String str);
}