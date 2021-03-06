/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.security.authorization.permission;

import java.util.Collections;
import java.util.Set;

import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.api.Tree;
import org.apache.jackrabbit.oak.security.privilege.PrivilegeConstants;

/**
 * AllPermissions... TODO
 */
public final class AllPermissions implements CompiledPermissions {

    private static final CompiledPermissions INSTANCE = new AllPermissions();

    private AllPermissions() {
    }

    public static CompiledPermissions getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean canRead(Tree tree) {
        return true;
    }

    @Override
    public boolean canRead(Tree tree, PropertyState property) {
        return true;
    }

    @Override
    public boolean isGranted(long permissions) {
        return true;
    }

    @Override
    public boolean isGranted(Tree tree, long permissions) {
        return true;
    }

    @Override
    public boolean isGranted(Tree parent, PropertyState property, long permissions) {
        return true;
    }

    @Override
    public boolean isGranted(String path, long permissions) {
        return true;
    }

    @Override
    public Set<String> getPrivileges(Tree tree) {
        return Collections.singleton(PrivilegeConstants.JCR_ALL);
    }

    @Override
    public boolean hasPrivileges(Tree tree, String... privilegeNames) {
        return true;
    }
}