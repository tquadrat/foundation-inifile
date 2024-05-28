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

import static java.lang.String.format;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.notExists;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Comparator.naturalOrder;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.inifile.internal.Group.checkGroupNameCandidate;
import static org.tquadrat.foundation.inifile.internal.Value.checkKeyCandidate;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.CommonConstants.UTF8;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.util.StringUtils.breakText;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.inifile.INIFile;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.StringConverter;
import org.tquadrat.foundation.util.stringconverter.PathStringConverter;

/**
 *  The implementation for
 *  {@link INIFile}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: INIFileImpl.java 1134 2024-05-20 16:53:16Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: INIFileImpl.java 1134 2024-05-20 16:53:16Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class INIFileImpl implements INIFile
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The clock that is used to determine the last update. This is changed
     *  only for testing purposes, the default is
     *  {@link Clock#systemDefaultZone()}.
     */
    private Clock m_Clock = Clock.systemDefaultZone();

    /**
     *  The comment for this file.
     */
    @SuppressWarnings( "StringBufferField" )
    private final StringBuilder m_Comment = new StringBuilder();

    /**
     *  The reference to file that is used to persist the contents.
     */
    private final Path m_File;

    /**
     *  The groups.
     */
    private final Map<String,Group> m_Groups = new LinkedHashMap<>();

    /**
     *  The time when the file was last updated.
     */
    private Instant m_LastUpdated;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The format String that is used for the last updated comment.
     */
    private static final String LAST_UPDATED_FORMAT;

    /**
     *  The pattern that is used for the last updated comment.
     */
    private static final Pattern LAST_UPDATED_PATTERN;

    static
    {
        @SuppressWarnings( "LocalVariableNamingConvention" )
        final var s = "# Last Update: ";
        LAST_UPDATED_FORMAT = format( "%s%%s", s );

        try
        {
            LAST_UPDATED_PATTERN = compile( format( "%s(.*)$", s ) );
        }
        catch( final PatternSyntaxException e )
        {
            throw new ExceptionInInitializerError( e );
        }
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  <p>{@summary Creates a new instance of {@code INIFileImpl}.}</p>
     *  <p>The constructor does not check whether the <i>file</i> argument is
     *  {@code null}; this has to be done by the factory methods
     *  {@link #create(Path)}
     *  and
     *  {@link #open(Path)}.</p>
     *  <p>But that this constructor is public allows simpler tests.</p>
     *
     *  @param  file    The file that holds the contents.
     */
    private INIFileImpl( final Path file )
    {
        m_File = file;
        m_LastUpdated = Instant.now( m_Clock );
    }   //  INIFileImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addComment( final String comment )
    {
        if( isNotEmptyOrBlank( comment ) ) m_Comment.append( comment );
    }   //  addComment()

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addComment( final String group, final String comment )
    {
        requireValidArgument( group, "group", Group::checkGroupNameCandidate );
        if( isNotEmptyOrBlank( comment ) )
        {
            @SuppressWarnings( "LocalVariableNamingConvention" )
            final var g = m_Groups.computeIfAbsent( group, this::createGroup );
            g.addComment( comment );
        }
    }   //  addComment()

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addComment( final String group, final String key, final String comment )
    {
        requireValidArgument( group, "group", Group::checkGroupNameCandidate );
        requireValidArgument( key, "key", Value::checkKeyCandidate );
        if( isNotEmptyOrBlank( comment ) )
        {
            @SuppressWarnings( "LocalVariableNamingConvention" )
            final var g = m_Groups.computeIfAbsent( group, this::createGroup );
            g.addComment( key, comment );
        }
    }   //  addComment()

    /**
     *  <p>{@summary Breaks the given String into chunks of
     *  {@value #LINE_LENGTH}
     *  characters.} All but the last chunk will end with a backslash
     *  (&quot;\&quot;).</p>
     *
     *  @param  s   The String to split.
     *  @return The stream with the chunks.
     */
    public static final Stream<String> breakString( final String s )
    {
        var remainder = requireNonNullArgument( s, "s" ).replaceAll( "\n", "\\\\n" );
        final var builder = Stream.<String>builder();
        while( remainder.length() > LINE_LENGTH )
        {
            //noinspection ConstantExpression
            builder.add( format( "%s\\", remainder.substring( 0, LINE_LENGTH ) ) );
            remainder = remainder.substring( LINE_LENGTH );
        }
        builder.add( remainder );

        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  breakString()

    /**
     *  <p>{@summary Creates an empty INI file.} If the file already exists, it
     *  will be overwritten without notice.</p>
     *  <p>The given file is used to store the value on a call to
     *  {@link #save()}.</p>
     *
     *  @param  file    The file.
     *  @return The new instance.
     *  @throws IOException The file cannot be created.
     */
    public static final INIFile create( final Path file ) throws IOException
    {
        final var retValue = new INIFileImpl( requireNonNullArgument( file, "file" ) );
        retValue.save();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  create()

    /**
     *  Creates a new instance of
     *  {@link Group}
     *  for the given name.
     *
     *  @param  group   The name of the group.
     *  @return The new instance.
     */
    private final Group createGroup( final String group )
    {
        /*
         * The argument check will be done by the constructor itself.
         */
        return new Group( group );
    }   //  createGroup()

    /**
     *  Dumps the contents to a String.
     *
     *  @return The contents in a String.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final String dumpContents()
    {
        final var joiner = new StringJoiner( "\n", EMPTY_STRING, "\n" );
        if( !m_Comment.isEmpty() )
        {
            splitComment( m_Comment ).forEach( joiner::add );
        }
        joiner.add( format( LAST_UPDATED_FORMAT, m_LastUpdated.toString() ) );
        final var retValue = m_Groups.values()
            .stream()
            .map( Group::toString )
            .collect( joining( EMPTY_STRING, joiner.toString(), EMPTY_STRING ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  dumpContents()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> getValue( final String group, final String key )
    {
        Optional<String> retValue = Optional.empty();
        @SuppressWarnings( "LocalVariableNamingConvention" )
        final var g = m_Groups.get( requireValidArgument( group, "group", Group::checkGroupNameCandidate ) );
        if( nonNull( g ) )
        {
            retValue = g.getValue( key )
                .map( Value::getValue );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final <T> Optional<T> getValue( final String group, final String key, final StringConverter<? extends T> stringConverter )
    {
        requireNonNullArgument( stringConverter, "stringConverter" );
        final Optional<T> retValue = getValue( group, key )
            .map( stringConverter::fromString );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasGroup( final String group )
    {
        return isNotEmptyOrBlank( group ) && checkGroupNameCandidate( group ) && m_Groups.containsKey( group );
    }   //  hasGroup()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasValue( final String group, final String key )
    {
        final var retValue = isNotEmptyOrBlank( group )
            && isNotEmptyOrBlank( key )
            && checkGroupNameCandidate( group )
            && checkKeyCandidate( key )
            && getValue( group, key ).isPresent();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  hasValue()

    /**
     *  Join all lines that end with a backslash with the next line on the
     *  list.
     *
     *  @param  lines   The input lines.
     *  @return The output.
     */
    private final List<String> joinLines( final Iterable<String> lines )
    {
        final List<String> retValue = new ArrayList<>();

        final var buffer = new StringBuilder();
        for( final var line : lines )
        {
            if( line.endsWith( "\\" ) )
            {
                buffer.append( line );
                buffer.setLength( buffer.length() - 1 );
            }
            else if( buffer.isEmpty() )
            {
                retValue.add( line );
            }
            else
            {
                buffer.append( line );
                retValue.add( buffer.toString() );
                buffer.setLength( 0 );
            }
        }
        if( !buffer.isEmpty() ) retValue.add( buffer.toString() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  joinLines()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Collection<Entry> listEntries()
    {
        final List<Entry> buffer = new ArrayList<>();
        for( final var group : m_Groups.values() )
        {
            for( final var key : group.getKeys() )
            {
                final var value = group.getValue( key );
                buffer.add( new Entry( group.getName(), key, value.map( Value::getValue ).orElse( null ) ) );
            }
        }
        buffer.sort( naturalOrder() );
        final var retValue = List.copyOf( buffer );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  listEntries()

    /**
     *  Opens the given INI file and reads its contents. If the file does not
     *  exist yet, a new, empty file will be created.
     *
     *  @param  file    The file.
     *  @return The new instance.
     *  @throws IOException A problem occurred when reading the file.
     */
    public static final INIFile open( final Path file ) throws IOException
    {
        final var retValue = new INIFileImpl( file );
        retValue.parse();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  open()

    /**
     *  Loads the content from the file into memory.
     *
     *  @throws IOException A problem occurred when reading the file.
     */
    private final void parse() throws IOException
    {
        if( exists( m_File ) )
        {
            final var lines = joinLines( readAllLines( m_File, UTF8 ) );
            parse( lines );
        }
    }   //  parse()

    /**
     *  Parses the given lines to the INI file contents.
     *
     *  @param  lines   The joined lines; the list does not contain any
     *      multi-line constructs.
     *  @throws IOException The structure is invalid.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OverlyComplexMethod"} )
    public final void parse( final List<String> lines ) throws IOException
    {
        Group currentGroup = null;
        final Collection<String> commentBuffer = new ArrayList<>();
        m_Comment.setLength( 0 );
        var commentsToBuffer = false;

        final var errorMessage = Lazy.use( () -> "'%s' has invalid structure".formatted( PathStringConverter.INSTANCE.toString( m_File ) ) );

        final var groupPattern = compile( "\\[(.*)]" );
        ScanLoop: for( final var line : requireNonNullArgument( lines, "lines" ) )
        {
            var matcher = LAST_UPDATED_PATTERN.matcher( line );
            if( matcher.matches() )
            {
                m_LastUpdated = Instant.parse( matcher.group( 1 ) );
                continue ScanLoop;
            }

            if( line.startsWith( "#" ) )
            {
                //---* Comment line found *------------------------------------
                @SuppressWarnings( "LocalVariableNamingConvention" )
                final var s = (line.length() > 1 ? line.substring( 1 ).trim() : EMPTY_STRING);
                if( commentsToBuffer )
                {
                    commentBuffer.add( s );
                }
                else
                {
                    m_Comment.append( s );
                }
                continue ScanLoop;
            }

            matcher = groupPattern.matcher( line );
            if( matcher.matches() )
            {
                final var name = matcher.group( 1 );
                currentGroup = createGroup( name );
                m_Groups.put( name, currentGroup );
                commentBuffer.forEach( currentGroup::addComment );
                commentBuffer.clear();
                continue ScanLoop;
            }

            if( line.isBlank() )
            {
                commentsToBuffer = true;
                continue ScanLoop;
            }

            if( isNull( currentGroup) ) throw new IOException( errorMessage.get() );
            final var pos = line.indexOf( '=' );
            if( pos < 1 ) throw new IOException( errorMessage.get() );
            final var key = line.substring( 0, pos ).trim();
            final var data = line.trim().length() > pos + 1
                ? line.substring( pos + 1 ).trim().translateEscapes()
                : null;
            final var value = currentGroup.setValue( key, data );
            commentBuffer.forEach( value::addComment );
            commentBuffer.clear();
        }   //  ScanLoop:
    }   //  parse()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void refresh() throws IOException { parse(); }

    /**
     *  Saves the contents of the INI file to the file that was provided to
     *  {@link #create(Path)}
     *  or
     *  {@link #open(Path)}.
     *
     *  @throws IOException  A problem occurred when writing the contents to
     *      the file.
     */
    @Override
    public final void save() throws IOException
    {
        if( notExists( m_File ) )
        {
            final var folder = m_File.getParent();
            if( notExists( folder ) ) createDirectories( folder );
        }
        m_LastUpdated = Instant.now( m_Clock );
        writeString( m_File, dumpContents(), UTF8, CREATE, TRUNCATE_EXISTING, WRITE );
    }   //  save()

    /**
     *  <p>{@summary Sets the clock that is used to determine the last
     *  update.}</p>
     *  <p>This is used only for testing purposes.</p>
     *
     *  @param  clock   The clock.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void setClock( final Clock clock )
    {
        m_Clock = requireNonNullArgument( clock, "clock" );
        m_LastUpdated = Instant.now( clock );
    }   //  setClock()

    /**
     * {@inheritDoc}
     *
     *  @since 0.4.3
     */
    @API( status = STABLE, since = "0.4.3" )
    @Override
    public final void setComment( final String comment )
    {
        m_Comment.setLength( 0 );
        addComment( comment );
    }   //  setComment()

    /**
     * {@inheritDoc}
     *
     *  @since 0.4.3
     */
    @API( status = STABLE, since = "0.4.3" )
    @Override
    public final void setComment( final String group, final String comment )
    {
        @SuppressWarnings( "LocalVariableNamingConvention" )
        final var g = m_Groups.computeIfAbsent( requireValidArgument( group, "group", Group::checkGroupNameCandidate ), this::createGroup );
        g.setComment( comment );
    }   //  setComment()

    /**
     * {@inheritDoc}
     *
     *  @since 0.4.3
     */
    @API( status = STABLE, since = "0.4.3" )
    @Override
    public final void setComment( final String group, final String key, final String comment )
    {
        @SuppressWarnings( "LocalVariableNamingConvention" )
        final var g = m_Groups.computeIfAbsent( requireValidArgument( group, "group", Group::checkGroupNameCandidate ), this::createGroup );
        g.setComment( requireValidArgument( key, "key", Value::checkKeyCandidate ), comment );
    }   //  setComment()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final void setValue( final String group, final String key, final String value )
    {
        @SuppressWarnings( "LocalVariableNamingConvention" )
        final var g = m_Groups.computeIfAbsent( requireValidArgument( group, "group", Group::checkGroupNameCandidate ), this::createGroup );
        g.setValue( key, value );
    }   //  setValue()

    /**
     *  Splits a comment to the proper line length.
     *
     *  @param  comment The comment to split.
     *  @return The lines for the comment.
     */
    public static final Collection<String> splitComment( final CharSequence comment )
    {
        final Collection<String> retValue = new ArrayList<>();

        if( !requireNonNullArgument( comment, "comment" ).isEmpty() )
        {
            //noinspection ConstantExpression
            breakText( comment, LINE_LENGTH - 2 )
                .map( "# "::concat )
                .map( String::trim )
                .forEach( retValue::add );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  splitComment()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return dumpContents(); }
}
//  class INIFileImpl

/*
 *  End of File
 */