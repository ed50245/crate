/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.integrationtests;

import io.crate.testing.SQLResponse;
import io.crate.testing.TestingHelpers;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

@ESIntegTestCase.ClusterScope(numClientNodes = 0, numDataNodes = 2, supportsDedicatedMasters = false)
public class SelectOrderByIntegrationTest extends SQLTransportIntegrationTest {

    @Before
    public void initTestData() throws Exception {
        Setup setup = new Setup(sqlExecutor);
        setup.partitionTableSetup();
        setup.groupBySetup();
    }

    @Test
    public void testSelectOrderByNullSortingASC() throws Exception {
        execute("select age from characters order by age");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("32\n" +
               "34\n" +
               "43\n" +
               "112\n" +
               "NULL\n" +
               "NULL\n" +
               "NULL\n"));
    }

    @Test
    public void testSelectOrderByNullSortingDESC() throws Exception {
        execute("select age from characters order by age desc");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("NULL\n" +
               "NULL\n" +
               "NULL\n" +
               "112\n" +
               "43\n" +
               "34\n" +
               "32\n"));
    }

    @Test
    public void testSelectOrderByNullSortingASCWithFunction() throws Exception {
        execute("select abs(age) from characters order by 1 asc");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("32\n" +
               "34\n" +
               "43\n" +
               "112\n" +
               "NULL\n" +
               "NULL\n" +
               "NULL\n"));
    }

    @Test
    public void testSelectOrderByNullSortingDESCWithFunction() throws Exception {
        execute("select abs(age) from characters order by 1 desc");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("NULL\n" +
               "NULL\n" +
               "NULL\n" +
               "112\n" +
               "43\n" +
               "34\n" +
               "32\n"));
    }


    @Test
    public void testSelectGroupByOrderByNullSortingASC() throws Exception {
        execute("select age from characters group by age order by age");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("32\n" +
               "34\n" +
               "43\n" +
               "112\n" +
               "NULL\n"));
    }

    @Test
    public void testSelectGroupByOrderByNullSortingDESC() throws Exception {
        execute("select age from characters group by age order by age desc");
        assertThat(TestingHelpers.printedTable(response.rows()),
            is("NULL\n" +
               "112\n" +
               "43\n" +
               "34\n" +
               "32\n"));
    }

    @Test
    public void testOrderByNullsFirstAndLast() throws Exception {
        SQLResponse response = execute(
            "select details['job'] from characters order by details['job'] nulls first limit 1");
        assertNull(response.rows()[0][0]);

        response = execute(
            "select details['job'] from characters order by details['job'] desc nulls first limit 1");
        assertNull(response.rows()[0][0]);

        response = execute(
            "select details['job'] from characters order by details['job'] nulls last");
        assertNull(response.rows()[((Long) response.rowCount()).intValue() - 1][0]);

        response = execute(
            "select details['job'] from characters order by details['job'] desc nulls last");
        assertNull(response.rows()[((Long) response.rowCount()).intValue() - 1][0]);


        response = execute(
            "select distinct details['job'] from characters order by details['job'] desc nulls last");
        assertNull(response.rows()[((Long) response.rowCount()).intValue() - 1][0]);
    }
}
