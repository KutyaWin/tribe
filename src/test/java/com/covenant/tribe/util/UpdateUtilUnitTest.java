package com.covenant.tribe.util;

import com.covenant.tribe.util.reflection.UpdateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateUtilUnitTest {
    @Test
    public void properly_updated() throws IllegalAccessException {
        UpdateTestObj nested = new UpdateTestObj("1", "2", null);
        UpdateTestObj objToUpdate = new UpdateTestObj("a", "b", nested);

        UpdateTestObj newNested = new UpdateTestObj("3", "4", null);
        UpdateTestObj newObj = new UpdateTestObj("c", "d", newNested);

        UpdateUtil.updateEntity(objToUpdate, newObj);
        UpdateUtil.updateEntity(objToUpdate.getChild(), newObj.getChild());
        assertEquals(objToUpdate, newObj);
    }

    @Test
    public void partially_updated() throws IllegalAccessException {
        UpdateTestObj nested = new UpdateTestObj("1", "2", null);
        UpdateTestObj objToUpdate = new UpdateTestObj("a", "b", nested);

        UpdateTestObj newNested = new UpdateTestObj("3", "4", null);
        UpdateTestObj newObj = new UpdateTestObj("c", null, newNested);

        UpdateUtil.updateEntity(objToUpdate, newObj);

        assertNotNull(objToUpdate.getSecondField());
    }

}
