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
package org.elasticsearch.shell.client.executors;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.shell.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Luca Cavanna
 *
 * {@link RequestExecutor} base implementation which contains the common methods to handle an execution
 * together with its {@link ActionResponse}, which needs to be converted to native json
 *
 * Why not using the already available elasticsearch RequestBuilders, which do pretty much the same?
 * Mostly because we don't want to expose the RequestBuilders to the shell since they require an es client and they are auto-executable.
 * We want to expose our client wrappers and only simple ActionRequest pojos to the shell. Since it's not possible to
 * create a RequestBuilder given an ActionRequest, we need this abstraction over ActionRequests. Therefore we can avoid
 * exposing and using RequestBuilders at all and always rely on the same mechanism, for both execution coming from the users and
 * internal executions.
 *
 * @param <Request> the type of the {@link ActionRequest}
 * @param <Response> the type of the {@link ActionResponse}
 * @param <JsonInput> the native json received as input, depending on the script engine in use
 * @param <JsonOutput> the native json returned as output, depending on the script engine in use
 */
public abstract class AbstractRequestExecutor<Request extends ActionRequest<Request>, Response extends ActionResponse, JsonInput, JsonOutput>
    implements RequestExecutor<Request, JsonOutput> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRequestExecutor.class);

    protected final Client client;
    protected final JsonSerializer<JsonInput, JsonOutput> jsonSerializer;

    protected AbstractRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer) {
        this.client = client;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public JsonOutput execute(Request request) {
        return responseToJson(request, doExecute(request).actionGet());
    }

    /**
     * Executes an elasticsearch {@link ActionRequest}
     * @param request  the request to execute
     * @return the result of the async execution as a Future
     */
    protected abstract ActionFuture<Response> doExecute(Request request);

    /**
     * Converts an elasticsearch {@link ActionResponse} to native json
     * @param request the request that generated the given response
     * @param response the response to be converted
     * @return the native json representation of the response
     */
    protected JsonOutput responseToJson(Request request, Response response) {
        try {
            return jsonSerializer.stringToJson(toXContent(request, response, initContentBuilder()).string());
        } catch (IOException e) {
            logger.error("Error while generating the XContent response", e);
            return null;
        }
    }

    protected XContentBuilder initContentBuilder() throws IOException {
        return JsonXContent.contentBuilder();
    }

    /**
     * Writes an elasticsearch {@link ActionResponse} to the given {@link XContentBuilder}
     * @param request the request that generated the given response
     * @param response the response that needs to be written out
     * @param builder the builder where to write the response
     * @return the builder
     * @throws IOException if there are problems while writing the response
     */
    protected abstract XContentBuilder toXContent(Request request, Response response, XContentBuilder builder) throws IOException;

    protected static final class Fields {
        public static final XContentBuilderString OK = new XContentBuilderString("ok");
        public static final XContentBuilderString _INDEX = new XContentBuilderString("_index");
        public static final XContentBuilderString _TYPE = new XContentBuilderString("_type");
        public static final XContentBuilderString _ID = new XContentBuilderString("_id");
        public static final XContentBuilderString _VERSION = new XContentBuilderString("_version");
        public static final XContentBuilderString MATCHES = new XContentBuilderString("matches");
        public static final XContentBuilderString FOUND = new XContentBuilderString("found");
        public static final XContentBuilderString COUNT = new XContentBuilderString("count");
        public static final XContentBuilderString GET = new XContentBuilderString("get");
        public static final XContentBuilderString MATCHED = new XContentBuilderString("matched");
        public static final XContentBuilderString EXPLANATION = new XContentBuilderString("explanation");
        public static final XContentBuilderString VALUE = new XContentBuilderString("value");
        public static final XContentBuilderString DESCRIPTION = new XContentBuilderString("description");
        public static final XContentBuilderString DETAILS = new XContentBuilderString("details");
        public static final XContentBuilderString ACKNOWLEDGED = new XContentBuilderString("acknowledged");
    }
}