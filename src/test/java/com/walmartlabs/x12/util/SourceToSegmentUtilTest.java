/**
Copyright (c) 2018-present, Walmart, Inc.

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

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SourceToSegmentUtilTest {

    private String sourceDataFromFile;

    @Before
    public void init() throws IOException {
        sourceDataFromFile = X12DocumentTestData.readFile("src/test/resources/dex/894/dex.sample.1.txt");
    }

    @Test
    public void test_splitSourceDataIntoSegments_Null() {
        String sourceData = null;
        
        List<X12Segment> segmentsList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData);
        assertNotNull(segmentsList);
        assertEquals(0, segmentsList.size());
    }

    @Test
    public void test_splitSourceDataIntoSegments_Empty() {
        String sourceData = "";
        
        List<X12Segment> segmentsList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData);
        assertNotNull(segmentsList);
        assertEquals(0, segmentsList.size());
    }
    
    @Test
    public void test_splitSourceDataIntoSegments_OneSegment() {
        String sourceData = "DXE*1*2";
        
        List<X12Segment> segmentsList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData);
        assertNotNull(segmentsList);
        assertEquals(1, segmentsList.size());
        assertEquals("DXE*1*2", segmentsList.get(0).toString());
    }

    @Test
    public void test_splitSourceDataIntoSegments() {
        List<X12Segment> segmentsList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceDataFromFile);
        assertNotNull(segmentsList);
        assertEquals(22, segmentsList.size());
        assertEquals("DXS*9251230013*DX*004010UCS*1*9254850000", segmentsList.get(0).toString());
        assertEquals("DXE*1*2", segmentsList.get(21).toString());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void test_splitSourceDataIntoSegments_null_delimiter() {
        Method method = this.findSplitSourceDataIntoSegments();
        List<X12Segment> segmentsList =  (List<X12Segment>) ReflectionUtils.invokeMethod(method, null, sourceDataFromFile, null);
        assertNotNull(segmentsList);
        assertEquals(0, segmentsList.size());
    }
    
    @Test(expected = PatternSyntaxException.class)
    public void test_splitSourceDataIntoSegments_partial_delimiter() {
        Method method = this.findSplitSourceDataIntoSegments();
        ReflectionUtils.invokeMethod(method, null, sourceDataFromFile, "\\");
    }
    
    @Test
    public void test_findElementDelimiterCharacter_null() {
        String sourceData = null;
        Method method = this.findElementDelimiterCharacter();
        Character delimiterChar =  (Character) ReflectionUtils.invokeMethod(method, null, sourceData);
        assertNull(delimiterChar);
    }
    
    @Test
    public void test_findElementDelimiterCharacter_sourceFile() {
        Method method = this.findElementDelimiterCharacter();
        Character delimiterChar =  (Character) ReflectionUtils.invokeMethod(method, null, sourceDataFromFile);
        assertNotNull(delimiterChar);
        assertEquals(new Character('*'), delimiterChar);
    }

    @Test
    public void test_findElementDelimiterCharacter_sourceIsaSegment() {
        String sourceData = "ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>";
        Method method = this.findElementDelimiterCharacter();
        Character delimiterChar =  (Character) ReflectionUtils.invokeMethod(method, null, sourceData);
        assertNotNull(delimiterChar);
        assertEquals(new Character('*'), delimiterChar);
    }
    
    @Test
    public void test_findElementDelimiterCharacter_sourceIsaSegmentNonDefaultChar() {
        String sourceData = "ISA~01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>";
        Method method = this.findElementDelimiterCharacter();
        Character delimiterChar =  (Character) ReflectionUtils.invokeMethod(method, null, sourceData);
        assertNotNull(delimiterChar);
        assertEquals(new Character('~'), delimiterChar);
    }
    
    @Test
    public void test_findElementDelimiterCharacter_sourceShorterThanExpected() {
        String sourceData = "ISA";
        Method method = this.findElementDelimiterCharacter();
        Character delimiterChar =  (Character) ReflectionUtils.invokeMethod(method, null, sourceData);
        assertNull(delimiterChar);
    }
    
    private Method findSplitSourceDataIntoSegments() {
        Method method = ReflectionUtils.findMethod(SourceToSegmentUtil.class, "splitSourceDataIntoSegments", String.class, String.class);
        method.setAccessible(true);
        return method;
    }
    
    private Method findElementDelimiterCharacter() {
        Method method = ReflectionUtils.findMethod(SourceToSegmentUtil.class, "findElementDelimiterCharacter", String.class);
        method.setAccessible(true);
        return method;
    }

}
