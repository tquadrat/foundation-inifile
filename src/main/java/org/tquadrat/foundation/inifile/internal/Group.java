/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static java.lang.Integer.signum;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.breakString;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.splitComment;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  The group for an INI file.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Group.java 1105 2024-02-28 12:58:46Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "NewClassNamingConvention" )
@ClassVersion( sourceVersion = "$Id: Group.java 1105 2024-02-28 12:58:46Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class Group implements Comparable<Group>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The comment for this group.
     */
    @SuppressWarnings( "StringBufferField" )
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
        m_Name = requireValidArgument( name, "name", Group::checkGroupNameCandidate );
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
        if( isNotEmptyOrBlank( comment ) ) m_Comment.append( comment );
    }   //  addComment()

    /**
     *  Adds a comment to the value with the given key.
     *
     *  @param  key The key.
     *  @param  comment The comment.
     */
    public final void addComment( final String key, final String comment )
    {
        requireValidArgument( key, "key", Value::checkKeyCandidate );
        if( nonNull( comment ) )
        {
            m_Values.computeIfAbsent( key, this::createValue )
                .addComment( comment );
        }
    }   //  addComment()

    /**
     *  <p>{@summary An implementation of
     *  {@link Predicate }
     *  to be used with
     *  {@link org.tquadrat.foundation.lang.Objects#requireValidArgument(Object,String,Predicate)}
     *  when checking the names for new groups.}</p>
     *  <p>The candidate may not contain newline characters, tab characters or
     *  closing brackets (']').</p>
     *  <p>You should avoid equal signs ('='), hash signs ('#') and opening
     *  brackets ('['), too, although they are technically allowed.</p>
     *
     *  @param  candidate   The key or name to check.
     *  @return {@code true} if the value is a valid name or key.
     *  @throws org.tquadrat.foundation.exception.NullArgumentException The
     *      candidate is {@code null}.
     *  @throws org.tquadrat.foundation.exception.EmptyArgumentException    The
     *      candidate is the empty string.
     *  @throws org.tquadrat.foundation.exception.BlankArgumentException    The
     *      candidate consists of whitespace only.
     *
     *  @since 0.4.4
     */
    @API( status = INTERNAL, since = "0.4.4" )
    public static final boolean checkGroupNameCandidate( final String candidate )
    {
        var retValue = requireNotBlankArgument( candidate, "candidate" ).indexOf( ']' ) < 0;
        if( retValue ) retValue = candidate.indexOf( '\n' ) < 0;
        if( retValue ) retValue = candidate.indexOf( '\t' ) < 0;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  checkGroupNameCandidate()

    /**
     *  {@inheritDoc}
     *  <p>This method is different from
     *  {@link #equals(Object) equals()}
     *  as it does not consider the comment.</p>
     *
     *  @since 0.4.2
     */
    @Override
    @API( status = MAINTAINED, since = "0.4.2" )
    public final int compareTo( final Group o )
    {
        final var retValue = signum( m_Name.compareTo( o.m_Name ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compareTo()

    /**
     *  Creates a new instance of
     *  {@link Value}
     *  for the given name.
     *
     *  @param  key The name of the value.
     *  @return The new instance.
     */
    private final Value createValue( final String key )
    {
        /*
         * The key argument will be checked by the constructor itself.
         */
        final var retValue = new Value( this, key );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && o instanceof final Group other )
        {
            retValue = m_Comment.toString().contentEquals( other.m_Comment )
                && m_Name.equals( other.m_Name )
                && m_Values.equals( other.m_Values );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns all keys for this group.
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
     *  Returns the name for this group.
     *
     *  @return The group's name.
     */
    public final String getName() { return m_Name; }

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
        final var retValue = Optional.ofNullable( m_Values.get( requireValidArgument( key, "key", Value::checkKeyCandidate ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return Objects.hash( m_Comment, m_Name, m_Values ); }

    /**
     *  <p>{@summary Sets a comment to the group.}</p>
     *  <p>Any previously existing comment will be overwritten.</p>
     *
     *  @param  comment The comment.
     *
     *  @since 0.4.2
     */
    @API( status = INTERNAL, since = "0.4.3" )
    public final void setComment( final String comment )
    {
        m_Comment.setLength( 0 );
        addComment( comment );
    }   //  setComment()

    /**
     *  <p>{@summary Sets a comment to the value with the given key.}</p>
     *  <p>Any previously existing comment will be overwritten.</p>
     *
     *  @param  key The key.
     *  @param  comment The comment.
     *
     *  @since 0.4.2
     */
    @API( status = INTERNAL, since = "0.4.3" )
    public final void setComment( final String key, final String comment )
    {
        final var value = m_Values.computeIfAbsent( requireValidArgument( key, "key", Value::checkKeyCandidate ), this::createValue );
        value.setComment( comment );
    }   //  setComment()

    /**
     *  Sets the given value for the given key.
     *
     *  @param  key The key.
     *  @param  value   The new value; can be {@code null}.
     *  @return The instance of
     *      {@link Value}
     *      for the new value.
     */
    public final Value setValue( final String key, final String value )
    {
        final var retValue = m_Values.computeIfAbsent( requireValidArgument( key, "key", Value::checkKeyCandidate ), this::createValue );
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
            .collect( joining( EMPTY_STRING, joiner.toString(), EMPTY_STRING ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class Group

/*
 *  End of File
 */