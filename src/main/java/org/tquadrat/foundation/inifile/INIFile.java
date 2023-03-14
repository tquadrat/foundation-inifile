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

package org.tquadrat.foundation.inifile;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.inifile.internal.INIFileImpl;
import org.tquadrat.foundation.lang.StringConverter;

/**
 *  The API for the access to Windows-style configuration files.
 *
 *  @note   Changes will not be persisted automatically!
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: INIFile.java 1052 2023-03-06 06:30:36Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: INIFile.java 1052 2023-03-06 06:30:36Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public sealed interface INIFile
    permits org.tquadrat.foundation.inifile.internal.INIFileImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  An entry for the INI file.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: INIFile.java 1052 2023-03-06 06:30:36Z tquadrat $
     *
     *  @UMLGraph.link
     *  @since 0.1.0
     */
    @SuppressWarnings( {"InnerClassOfInterface", "NewClassNamingConvention"} )
    public record Entry( String group, String key, String value )
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Retrieves the value and translates it to the desired type.
         *
         *  @param  <T> The target type.
         *  @param  stringConverter The implementation of
         *      {@link StringConverter}
         *      that is used to convert the stored value into the target type.
         *  @return The value; can be {@code null}.
         */
        public final <T> T value( final StringConverter<? extends T> stringConverter )
        {
            final var retValue = requireNonNullArgument( stringConverter, "stringConverter" ).fromString( value() );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  value()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final String toString() { return format( "%s/%s = %s", group, key, value ); }
    }
    //  record Entry

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The line length for an INI file: {@value}.
     */
    public static final int LINE_LENGTH = 75;

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Adds the given comment to the INI file.}</p>
     *  <p>The new comment will be appended to an already existing one.</p>
     *
     *  @param  comment The comment.
     */
    public void addComment( final String comment );

    /**
     *  <p>{@summary Adds the given comment to the given group.}</p>
     *  <p>The new comment will be appended to an already existing one.</p>
     *
     *  @param  group   The group.
     *  @param  comment The comment.
     */
    public void addComment( final String group, final String comment );

    /**
     *  <p>{@summary Adds the given comment to the value that is identified by
     *  the given group and key.}</p>
     *  <p>The new comment will be appended to an already existing one.</p>
     *
     *  @param  group   The group.
     *  @param  key The key for the value.
     *  @param  comment The comment.
     */
    public void addComment( final String group, final String key, final String comment );

    /**
     *  <p>{@summary Creates an empty INI file.} If the file already exists, it
     *  will be overwritten without notice.</p>
     *  <p>The given file is used to store the value on a call to
     *  {@link #save()}.</p>
     *
     *  @param  file    The file.
     *  @return The new instance.
     */
    public static INIFile create( final Path file ) { return INIFileImpl.create( file ); }

    /**
     *  Retrieves the value for the given key from the given group.
     *
     *  @param  group   The group.
     *  @param  key The key for the value.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the retrieved value.
     */
    public Optional<String> getValue( final String group, final String key );

    /**
     *  Retrieves the value for the given key from the given group.
     *
     *  @param  group   The group.
     *  @param  key The key for the value.
     *  @param  defaultValue    The value that will be returned if no other
     *      value could be found; can be {@code null}.
     *  @return The value.
     */
    public default String getValue( final String group, final String key, final String defaultValue )
    {
        final var retValue = getValue( group, key ).orElse( defaultValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  Retrieves the value for the given key from the given group.
     *
     *  @param  <T> The target type.
     *  @param  group   The group.
     *  @param  key The key for the value.
     *  @param  stringConverter The implementation of
     *      {@link StringConverter}
     *      that is used to convert the stored value into the target type.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the retrieved value.
     */
    public <T> Optional<T> getValue( final String group, final String key, final StringConverter<? extends T> stringConverter );

    /**
     *  Retrieves the value for the given key from the given group.
     *
     *  @param  <T> The target type.
     *  @param  group   The group.
     *  @param  key The key for the value.
     *  @param  stringConverter The implementation of
     *      {@link StringConverter}
     *      that is used to convert the stored value into the target type.
     *  @param  defaultValue    The value that will be returned if no other
     *      value could be found; can be {@code null}.
     *  @return The value.
     */
    public default <T> T getValue( final String group, final String key, final StringConverter<T> stringConverter, final T defaultValue )
    {
        final var retValue = getValue( group, key, stringConverter ).orElse( defaultValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getValue()

    /**
     *  Checks whether the INI file contains a group with the given name.
     *
     *  @param  group   The group.
     *  @return {@code true} if there is a group with the given name,
     *      {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public boolean hasGroup( final String group );

    /**
     *  Checks whether the INI file contains an entry with the given key.
     *
     *  @param  group   The group.
     *  @param  key The key.
     *  @return {@code true} if there is an entry with the given key,
     *      {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public boolean hasValue( final String group, final String key );

    /**
     *  Returns all entries of the INI file.
     *
     *  @return The entries.
     */
    public Collection<Entry> listEntries();

    /**
     *  Loads the entries for the INI file.
     *
     *  @param  entries The entries.
     */
    public default void loadEntries( final Entry... entries )
    {
        for( final var entry : requireNonNullArgument( entries, "entries" ) )
        {
            setValue( entry.group(), entry.key(), entry.value() );
        }
    }   //  loadEntries()

    /**
     *  Loads the entries for the INI file.
     *
     *  @param  entries The entries.
     */
    public default void loadEntries( final Collection<Entry> entries )
    {
        loadEntries( entries.toArray( Entry []::new ) );
    }   //  loadEntries()

    /**
     *  Opens the given INI file and reads its contents. If the file does not
     *  exist yet, a new, empty file will be created.
     *
     *  @param  file    The file.
     *  @return The new instance.
     *  @throws IOException A problem occurred when reading the file.
     */
    public static INIFile open( final Path file ) throws IOException { return INIFileImpl.open( file ); }

    /**
     *  Re-reads the values.
     *
     *  @throws IOException A problem occurred when reading the file.
     */
    public void refresh() throws IOException;

    /**
     *  Saves the contents of the INI file to the file that was provided to
     *  {@link #create(Path)}
     *  or
     *  {@link #open(Path)}.
     *  This method has to be called to persist any changes made to the
     *  contents of the INI file.
     *
     *  @throws IOException  A problem occurred when writing the contents to
     *      the file.
     */
    public void save() throws IOException;

    /**
     *  Stores the given value with the given key to the given group.
     *
     *  @param  group   The group.
     *  @param  key The key.
     *  @param  value   The value.
     */
    public void setValue( final String group, final String key, final String value );

    /**
     *  Stores the given value with the given key to the given group.
     *
     *  @param  <S> The source type for the value.
     *  @param  group   The group.
     *  @param  key The key.
     *  @param  value   The value.
     *  @param  stringConverter The instance of
     *      {@link StringConverter}
     *      that is used to convert the value to a String.
     */
    public default <S> void setValue( final String group, final String key, final S value, final StringConverter<S> stringConverter )
    {
        setValue( group, key, requireNonNullArgument( stringConverter, "stringConverter" ).toString( value ) );
    }   //  setValue()
}
//  interface INIFile

/*
 *  End of File
 */