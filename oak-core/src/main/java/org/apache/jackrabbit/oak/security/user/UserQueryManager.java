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
package org.apache.jackrabbit.oak.security.user;

import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.security.user.query.XPathQueryBuilder;
import org.apache.jackrabbit.oak.security.user.query.XPathQueryEvaluator;
import org.apache.jackrabbit.oak.spi.security.user.AuthorizableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserQueryManager... TODO
 */
class UserQueryManager {

    /**
     * logger instance
     */
    private static final Logger log = LoggerFactory.getLogger(UserQueryManager.class);

    private final UserManagerImpl userManager;
    private final Root root;
    private final QueryManager queryManager;

    // TODO: replace usage of jcr-query-manager by oak query manager and drop session from constructor.
    UserQueryManager(UserManagerImpl userManager, Session session, Root root) throws RepositoryException {
        this.userManager = userManager;
        this.root = root;
        this.queryManager = (session != null) ? session.getWorkspace().getQueryManager() : null;
    }

    Iterator<Authorizable> find(Query query) throws RepositoryException {
        // TODO: create query builder depending query-language configured with user-mgt configuration.
        if (queryManager != null) {
            XPathQueryBuilder builder = new XPathQueryBuilder();
            query.build(builder);
            return new XPathQueryEvaluator(builder, userManager, queryManager, userManager.getNamePathMapper()).eval();
        } else {
            // TODO: implement
            throw new UnsupportedOperationException("not implemented");
        }
    }

    @Nonnull
    Iterator<Authorizable> findAuthorizables(String relativePath, String value, AuthorizableType authorizableType) {
        String[] oakPaths =  new String[] {userManager.getNamePathMapper().getOakPath(relativePath)};
        return findAuthorizables(oakPaths, value, null, true, Long.MAX_VALUE, authorizableType);
    }

    /**
     * Find the authorizable trees matching the following search parameters within
     * the sub-tree defined by an authorizable tree:
     *
     * @param propertyRelPaths An array of property names or relative paths
     * pointing to properties within the tree defined by a given authorizable node.
     * @param value The property value to look for.
     * @param ntNames An array of node type names to restrict the search within
     * the authorizable tree to a subset of nodes that match any of the node
     * type names; {@code null} indicates that no filtering by node type is
     * desired. Specifying a node type name that defines an authorizable node
     * )e.g. {@link org.apache.jackrabbit.oak.spi.security.user.UserConstants#NT_REP_USER rep:User} will limit the search to
     * properties defined with the authorizable node itself instead of searching
     * the complete sub-tree.
     * @param exact A boolean flag indicating if the value must match exactly or not.s
     * @param maxSize The maximal number of search results to look for.
     * @param authorizableType Filter the search results to only return authorizable
     * trees of a given type. Passing {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableType#AUTHORIZABLE} indicates that
     * no filtering for a specific authorizable type is desired. However, properties
     * might still be search in the complete sub-tree of authorizables depending
     * on the other query parameters.
     * @return An iterator of authorizable trees that match the specified
     * search parameters and filters or an empty iterator if no result can be
     * found.
     */
    @Nonnull
    Iterator<Authorizable> findAuthorizables(String[] propertyRelPaths, String value, String[] ntNames, boolean exact, long maxSize, AuthorizableType authorizableType) {
        // TODO
        throw new UnsupportedOperationException("not yet implemented");

        //return AuthorizableIterator.create(result, this);

    }
}