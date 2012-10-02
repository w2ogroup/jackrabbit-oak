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
package org.apache.jackrabbit.mongomk.util;

import java.util.Arrays;

import org.apache.jackrabbit.mongomk.impl.MongoConnection;
import org.apache.jackrabbit.mongomk.model.CommitMongo;
import org.apache.jackrabbit.mongomk.model.HeadMongo;
import org.apache.jackrabbit.mongomk.model.NodeMongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Utility classes for preparing the {@code MongoDB} environement.
 *
 * @author <a href="mailto:pmarx@adobe.com>Philipp Marx</a>
 */
@SuppressWarnings("javadoc")
public class MongoUtil {

    public static final String INITIAL_COMMIT_MESSAGE = "This is an autogenerated initial commit";
    public static final String INITIAL_COMMIT_PATH = "";
    public static final String INITIAL_COMMIT_DIFF = "+\"/\" : {}";

    public static void clearCommitCollection(MongoConnection mongoConnection) {
        DBCollection commitCollection = mongoConnection.getCommitCollection();
        commitCollection.drop();
    }

    public static void clearDatabase(MongoConnection mongoConnection) {
        clearNodeCollection(mongoConnection);
        clearCommitCollection(mongoConnection);
        clearHeadCollection(mongoConnection);
    }

    public static void clearHeadCollection(MongoConnection mongoConnection) {
        DBCollection headCollection = mongoConnection.getHeadCollection();
        headCollection.drop();
    }

    public static void clearNodeCollection(MongoConnection mongoConnection) {
        DBCollection nodeCollection = mongoConnection.getNodeCollection();
        nodeCollection.drop();
    }

    public static void initCommitCollection(MongoConnection mongoConnection) {
        DBCollection commitCollection = mongoConnection.getCommitCollection();
        DBObject index = new BasicDBObject();
        index.put(CommitMongo.KEY_REVISION_ID, 1L);
        DBObject options = new BasicDBObject();
        options.put("unique", Boolean.TRUE);
        commitCollection.ensureIndex(index, options);
        CommitMongo commit = new CommitMongo();
        commit.setAffectedPaths(Arrays.asList(new String[] { "/" }));
        commit.setBaseRevId(0L);
        commit.setDiff(INITIAL_COMMIT_DIFF);
        commit.setMessage(INITIAL_COMMIT_MESSAGE);
        commit.setPath(INITIAL_COMMIT_PATH);
        commit.setRevisionId(0L);
        commitCollection.insert(commit);
    }

    public static void initDatabase(MongoConnection mongoConnection) {
        clearDatabase(mongoConnection);

        initNodeCollection(mongoConnection);
        initCommitCollection(mongoConnection);
        initHeadCollection(mongoConnection);
    }

    public static void initHeadCollection(MongoConnection mongoConnection) {
        DBCollection headCollection = mongoConnection.getHeadCollection();
        HeadMongo headMongo = new HeadMongo();
        headMongo.setHeadRevisionId(0L);
        headMongo.setNextRevisionId(1L);
        headCollection.insert(headMongo);
    }

    public static void initNodeCollection(MongoConnection mongoConnection) {
        DBCollection nodeCollection = mongoConnection.getNodeCollection();
        DBObject index = new BasicDBObject();
        index.put(NodeMongo.KEY_PATH, 1L);
        index.put(NodeMongo.KEY_REVISION_ID, 1L);
        DBObject options = new BasicDBObject();
        options.put("unique", Boolean.TRUE);
        nodeCollection.ensureIndex(index, options);
        NodeMongo root = new NodeMongo();
        root.setRevisionId(0L);
        root.setPath("/");
        nodeCollection.insert(root);
    }

    public static String fromMongoRepresentation(Long revisionId) {
        return String.valueOf(revisionId);
    }

    public static Long toMongoRepresentation(String revisionId) {
        return revisionId != null? Long.parseLong(revisionId) : null;
    }
}