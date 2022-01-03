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

/**
 *  <p>{@summary The module <i>INIFile</i> provides an API to Windows-style
 *  configuration files, the so called 'INI' files, from the extension
 *  '{@code .ini}' of these files.}</p>
 *  <p>These files are text files with UTF-8 encoding and do look like
 *  below:</p>
 *  <pre><code>
 *  # This is a comment line
 *  [Group]
 *  key=value
 *
 *  [otherGroup]
 *  key=value
 *  otherkey=otherValue
 *  key1=longvalue\
 *  continued in the next line
 *  text=Values may contain escape sequences like \n for new lines, \
 *  and \t for tabs.
 *  </code></pre>
 *  <ul>
 *      <li>Empty lines are ignored.</li>
 *      <li>A line that ends with a backslash (&quot;\&quot;) will be concatenated with the following line.</li>
 *      <li>All leading whitespace will be removed.</li>
 *      <li>Lines starting with the hash symbol (&quot;#&quot;) contain comments and will be ignored.</li>
 *      <li>All values are assigned to a group; this means that the first relevant line is always a group header.</li>
 *      <li>A group header is an arbitrary String within brackets (&quot;[&quot; and &quot;]&quot;).</li>
 *      <li>The keys are separated from the values by the equals sign (&quot;=&quot;).</li>
 *      <li>A key may contain whitespace (blanks), but trailing or leading whitespace is ignored.</li>
 *      <li>Any leading whitespace for a value will be ignored, but the escape sequence '\ ' for a blank will be regarded.</li>
 *  </ul>
 */

package org.tquadrat.foundation.inifile;

/*
 *  End of File
 */