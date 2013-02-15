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
package org.elasticsearch.shell.client.builders.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.client.builders.AbstractRequestBuilder;
import org.elasticsearch.shell.json.JsonSerializer;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * Request builder for create index API
 */
@SuppressWarnings("unused")
public class CreateIndexRequestBuilder<JsonInput, JsonOutput> extends AbstractRequestBuilder<CreateIndexRequest, CreateIndexResponse, JsonInput, JsonOutput> {

    public CreateIndexRequestBuilder(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        super(client, new CreateIndexRequest(null), jsonSerializer);
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> index(String index) {
        request.index(index);
        return this;
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> settings(JsonInput source) {
        request.settings(jsonToString(source));
        return this;
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> mapping(String type, JsonInput source) {
        request.mapping(type, jsonToString(source));
        return this;
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> cause(String cause) {
        request.cause(cause);
        return this;
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> source(JsonInput source) {
        request.source(jsonToString(source));
        return this;
    }

    public CreateIndexRequestBuilder<JsonInput, JsonOutput> timeout(String timeout) {
        request.timeout(timeout);
        return this;
    }

    @Override
    protected ActionFuture<CreateIndexResponse> doExecute(CreateIndexRequest request) {
        return client.admin().indices().create(request);
    }

    @Override
    protected XContentBuilder toXContent(CreateIndexRequest request, CreateIndexResponse response, XContentBuilder builder) throws IOException {
        return builder.startObject()
                .field(Fields.OK, true)
                .field(Fields.ACKNOWLEDGED, response.acknowledged())
                .endObject();
    }
}