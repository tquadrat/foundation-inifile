/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.inifile.internal;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.breakString;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.splitComment;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.StringJoiner;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The container for the value of an INI file.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Value.java 937 2021-12-14 21:59:00Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: Value.java 937 2021-12-14 21:59:00Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public final class Value
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The comment for this value.
     */
    private final StringBuilder m_Comment = new StringBuilder();

    /**
     *  The reference to the parent group.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final Group m_Group;

    /**
     *  The key.
     */
    private final String m_Key;

    /**
     *  The value itself.
     */
    private String m_Value;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code Value}.
     *
     *  @param  parent  The parent group.
     *  @param  key The key.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public Value( final Group parent, final String key )
    {
        m_Group = requireNonNullArgument( parent, "parent" );
        m_Key = requireNotEmptyArgument( key, "key" );
    }   //  Value()

    /**
     *  Creates a new instance of {@code Value}.
     *
     *  @param  parent  The parent group.
     *  @param  key The key.
     *  @param  value   The value.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public Value( final Group parent, final String key, final String value )
    {
        this( parent, key );
        setValue( value );
    }   //  Value()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a comment to the value.
     *
     *  @param  comment The comment.
     */
    public final void addComment( final String comment )
    {
        if( nonNull( comment ) ) m_Comment.append( comment );
    }   //  addComment()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"EqualsOnSuspiciousObject", "NonFinalFieldReferenceInEquals"} )
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = (this == o);
        if( !retValue && o instanceof Value other )
        {
            retValue = m_Comment.equals( other.m_Comment )
                && m_Group.equals( other.m_Group )
                && m_Key.equals( other.m_Key )
                && Objects.equals( m_Value, other.m_Value );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the current value.
     *
     *  @return The current value; can be {@code null}.
     */
    public final String getValue() { return m_Value; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "NonFinalFieldReferencedInHashCode" )
    @Override
    public final int hashCode() { return hash( m_Comment, m_Group, m_Key, m_Value ); }

    /**
     *  Sets a new value.
     *
     *  @param  value   The new value; can be {@code null}.
     */
    public final void setValue( final String value ) { m_Value = value; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        final var buffer = new StringJoiner( "\n", EMPTY_STRING, "\n" );
        if( !m_Comment.isEmpty() )
        {
            buffer.add( EMPTY_STRING );
            splitComment( m_Comment ).forEach( buffer::add );
        }
        breakString( format( "%s = %s", m_Key, m_Value ) ).forEach( buffer::add );
        final var retValue = buffer.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class Value

/*
 *  End of File
 */