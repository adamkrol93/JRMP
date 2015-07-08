/*
 * Licensed to AMG.net under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * AMG.net licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.amg.jira.plugins.jrmp.services.model;

/**
 * @author Adam Król
 */
public enum DateModel {
    TODAY("0"),YESTERDAY("-1d"),WEEK_AGO("-1w"),TWO_WEEKS_AGO("-2w"),MONTH_AGO("-4w"),THREE_MONTHS_AGO("-12w"),SIX_MONTHS_AGO("-180d"),YEAR_AGO("-365d");

    private String beforeValue;

    DateModel(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getBeforeValue() {
        return beforeValue;
    }
}
