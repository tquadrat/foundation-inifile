/*
 * ============================================================================
 *  Copyright © 2002-2022 by Thomas Thrien.
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

import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.notExists;
import static java.util.Objects.requireNonNull;
import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.apiguardian.api.API;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.inifile.INIFile;
import org.tquadrat.foundation.lang.AutoLock;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Issues when the INI file does not exist.
 *
 *  @version $Id: BugHunt_20220206_001.java 1015 2022-02-09 08:25:36Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20220206_001.java 1015 2022-02-09 08:25:36Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "org.tquadrat.foundation.inifile.internal.BugHunt_20220206_001" )
public class BugHunt_20220206_001 extends TestBaseClass
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The playground.
     */
    private Path m_Playground = null;

    /**
     *  The guard for the playground.
     */
    private final AutoLock m_PlaygroundGuard = AutoLock.of();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @BeforeEach
    @AfterEach
    final void cleanup()
    {
        m_PlaygroundGuard.execute( () ->
        {
            if( nonNull( m_Playground ) )
            {
                try
                {
                    deleteFolder( m_Playground );
                }
                catch( final IOException e )
                {
                    throw new AssertionError( e );
                }
            }
            m_Playground = null;
        } );
    }   //  cleanup()

    /**
     *  Deletes the given folder and all containing files; calls itself
     *  recursively on contained folders.
     *
     *  @param  folder  The folder to delete.
     *  @throws IOException A problem occurred on deleting the folder or its
     *      contents.
     */
    private final void deleteFolder( final Path folder ) throws IOException
    {
        if( Files.isDirectory( requireNonNull( folder, "folder is null" ) ) )
        {
            //---* Delete the folder contents *--------------------------------
            try( final var files = Files.list( folder ) )
            {
                for( final var file : files.toList() )
                {
                    if( !Files.isDirectory( file ) )
                    {
                        //---* Plain files will be deleted immediately *-------
                        Files.deleteIfExists( file );
                    }
                    else
                    {
                        //---* Get rid of the folders … *----------------------
                        deleteFolder( file );
                    }
                }
            }
        }

        //---* Delete the folder itself *--------------------------------------
        Files.deleteIfExists( folder );
    }   //  deleteFolder()

    /**
     *  Test how the library behaves for a missing file.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testMissingFileAndFolder() throws Exception
    {
        skipThreadTest();

        try( final var ignore = m_PlaygroundGuard.lock() )
        {
            //---* Prepare the playground *------------------------------------
            m_Playground = createTempDirectory( format( "Test-%s", Instant.now() ) );
            assertTrue( exists( m_Playground ) );

            //---* Define a non-existing file *--------------------------------
            final var notExisting = m_Playground.resolve( Path.of( "folder", "does", "not", "exist", "file.txt" ) );
            assertTrue( notExists( notExisting ) );

            //---* Open the INIFile with the not existing file *---------------
            final var inifile = INIFile.open( notExisting );
            assertNotNull( inifile );
            assertTrue( notExists( notExisting ) );

            //---* Save the INIFile *------------------------------------------
            inifile.addComment( "Test" );
            inifile.save();
            assertTrue( exists( notExisting ) );
        }
    }   //  testMissingFileAndFolder()
}
//  class BugHunt_20220206_001

/*
 *  End of File
 */