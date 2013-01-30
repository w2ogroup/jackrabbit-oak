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
package org.apache.jackrabbit.oak.plugins.segment;

import java.util.UUID;

import com.google.common.base.Preconditions;

public final class RecordId {

    private final UUID segmentId;

    private final int offset;

    public RecordId(UUID segmentId, int offset) {
        this.segmentId = Preconditions.checkNotNull(segmentId);
        this.offset = offset;
    }

    public UUID getSegmentId() {
        return segmentId;
    }

    public int getOffset() {
        return offset;
    }

    //------------------------------------------------------------< Object >--

    @Override
    public String toString() {
        return segmentId + ":" + offset;
    }

    @Override
    public int hashCode() {
        return segmentId.hashCode() ^ offset;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof RecordId) {
            RecordId that = (RecordId) object;
            return offset == that.offset && segmentId.equals(that.segmentId);
        } else {
            return false;
        }
    }

}