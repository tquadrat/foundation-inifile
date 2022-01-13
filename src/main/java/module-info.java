/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 *  Foundation <i>IniFile</i> provides the handling of Windows INI files
 *  to the Foundation library set.
 *
 *  @todo task.list
 */
module org.tquadrat.foundation.inifile
{
    requires java.base;

    //---* The foundation modules *--------------------------------------------
    requires transitive org.tquadrat.foundation.base;
    requires transitive org.tquadrat.foundation.util;

    //---* The exports *-------------------------------------------------------
    exports org.tquadrat.foundation.inifile;
}

/*
 *  End of File
 */