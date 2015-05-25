/*Copyright 2015 AMG.net

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.amg.jira.plugins.jrmp.rest;

/**
 * @author Adam Król
 */
public enum GadgetFieldEnum {
    FILTER("Filter"),DATE("Date"), TITLE("Title"), REFRESH("refresh");

    private String fieldName;

    GadgetFieldEnum(String fieldName) {
        this.fieldName = fieldName;
    }



    @Override
    public String toString() {
        return fieldName;
    }
}
