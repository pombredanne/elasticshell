/*
 * Licensed to Luca Cavanna (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.shell.console.completer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Luca Cavanna
 *
 * Helper that extracts the name of the objects that are useful to provide suggestions
 * from the current user input, given also the cursor position
 *
 */
public class NamesExtractor {

    /**
     * Extracts the name of the objects that are useful in order to provide suggestions
     * given the current user input and the cursor position.
     * Strips out the dots and the arguments from any function call
     * e.g. Given Requests.indexRequests.index('index_name').ty will return [Requests,indexRequest,index,ty]
     * @param buffer the user input
     * @param cursor the cursor position
     * @return the list of the names useful to provide suggestions
     */
    public List<String> extractNames(String buffer, int cursor) {

        int m = cursor - 1;

        //Looks backward and collects a list of identifiers
        //We stop as soon as we find something that is not a java identifier part or . or ).
        //We also strip out the arguments from any function call
        List<String> names = new ArrayList<String>();
        StringBuilder name = new StringBuilder();
        while (m >= 0) {
            char c = buffer.charAt(m);
            if (!Character.isJavaIdentifierPart(c)) {

                //no need to reverse if it's empty or only one char
                names.add(name.length() > 1 ? name.reverse().toString() : name.toString());
                name.setLength(0);

                if (c != '.') {
                    break;
                }

                int m2 = m - 1;
                //if we've found ). we then ignore all the arguments from the function call, till the previous open bracket
                if (m2 > 0 && buffer.charAt(m2) == ')') {
                    m -= 2;
                    int innerBrackets = 0;
                    while (m >= 0) {
                        char c2 = buffer.charAt(m);
                        if (c2 == ')') {
                            innerBrackets++;
                        }
                        if (c2 == '(' && innerBrackets-- == 0) {
                            break;
                        }
                        m--;
                    }
                }

            } else {
                name.append(c);
            }

            m--;
        }

        if (name.length() > 0 || names.isEmpty()) {
            //adding the last name
            names.add(name.length() > 1 ? name.reverse().toString() : name.toString());
        }

        //no need to reverse if empty or only one element
        if (names.size() > 1) {
            Collections.reverse(names);
        }

        return names;
    }
}