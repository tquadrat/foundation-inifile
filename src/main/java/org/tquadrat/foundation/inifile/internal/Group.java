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

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.breakString;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.splitComment;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  The group for an INI file.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Group.java 980 2022-01-06 15:29:19Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: Group.java 980 2022-01-06 15:29:19Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class Group
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The comment for this group.
     */
    private final StringBuilder m_Comment = new StringBuilder();

    /**
     *  The name of the group.
     */
    private final String m_Name;

    /**
     *  The values for this group.
     */
    private final Map<String,Value> m_Values = new TreeMap<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance for {@code Group}.
     *
     *  @param  name    The name for the group.
     */
    public Group( final String name )
    {
        m_Name = requireNotEmptyArgument( name, "name" );
    }   //  Group()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a comment to the group.
     *
     *  @param  comment The comment.
     */
    public final void addComment( final String comment )
    {
        if( nonNull( comment ) ) m_Comment.append( comment );
    }   //  addComment()

    /**
     *  Adds a comment to the value with the given key.
     *
     *  @param  key The key.
     *  @param  comment The comment.
     */
    public final void addComment( final String key, final String comment )
    {
        requireNotEmptyArgument( key, "key" );
        if( nonNull( comment ) )
        {
            m_Values.computeIfAbsent( requireNotEmptyArgument( key, "key" ), this::createValue )
                .addComment( comment );
        }
    }   //  addComment()

    /**
     *  Creates a new instance of
     *  {@link Value}
     *  for the given name.
     *
     *  @param  key The name of the value.
     *  @return The new instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final Value createValue( final String key )
    {
        return new Value( this, requireNotEmptyArgument( key, "key" ) );
    }   //  createValue()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "EqualsOnSuspiciousObject" )
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && o instanceof Group other )
        {
            retValue = m_Comment.equals( other.m_Comment )
                && m_Name.equals( other.m_Name )
                && m_Values.equals( other.m_Values );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns all key for this group.
     *
     *  @return The keys.
     */
    public final Collection<String> getKeys()
    {
        final var retValue = Set.copyOf( m_Values.keySet() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getKeys()

    /**
     *  Returns the value for the given key from this group.
     *
     *  @param  key The key.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the value.
     */
    public final Optional<Value> getValue( final String key )
    {
        final var retValue = Optional.ofNullable( m_Values.get( requireNotEmptyArgument( key, "key" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return Objects.hash( m_Comment, m_Name, m_Values ); }

    /**
     *  Sets the given value for the given key.
     *
     *  @param  key The key.
     *  @param  value   The new value; can be {@code null}.
     *  @return The instance of
     *      {@link Value}
     *      for the new value.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public final Value setValue( final String key, final String value )
    {
        final var retValue = m_Values.computeIfAbsent( requireNotEmptyArgument( key, "key" ), this::createValue );
        retValue.setValue( value );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  setValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        final var joiner = new StringJoiner( "\n", EMPTY_STRING, "\n" );
        joiner.add( EMPTY_STRING );
        if( !m_Comment.isEmpty() )
        {
            splitComment( m_Comment ).forEach( joiner::add );
        }
        breakString( format( "[%s]", m_Name ) ).forEach( joiner::add );
        final var retValue = m_Values.values()
            .stream()
            .map( Value::toString )
            .collect( Collectors.joining( EMPTY_STRING, joiner.toString(), EMPTY_STRING ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class Group

/*
 *  End of File
 */