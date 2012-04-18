/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.query.typesafe;

/**
 * Representation of an Enum in a query.
 * 
 * @param <T> Enum type
 */
public interface EnumExpression<T> extends ComparableExpression<Enum>
{
    /**
     * Method to return an expression for the ordinal of this enum.
     * @return Expression for the ordinal of the passed enum
     */
    NumericExpression ordinal();
}
