/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.queue.impl.client;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.queue.impl.AddAllOperation;
import com.hazelcast.queue.impl.QueuePortableHook;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides the request service for {@link com.hazelcast.queue.impl.AddAllOperation}
 */
public class AddAllRequest extends QueueRequest {

    private Collection<Data> dataList;

    public AddAllRequest() {
    }

    public AddAllRequest(String name, Collection<Data> dataList) {
        super(name);
        this.dataList = dataList;
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAllOperation(name, dataList);
    }

    @Override
    public int getClassId() {
        return QueuePortableHook.ADD_ALL;
    }

    @Override
    public void write(PortableWriter writer) throws IOException {
        super.write(writer);
        writer.writeInt("s", dataList.size());
        final ObjectDataOutput out = writer.getRawDataOutput();
        for (Data data : dataList) {
            data.writeData(out);
        }
    }

    @Override
    public void read(PortableReader reader) throws IOException {
        super.read(reader);
        int size = reader.readInt("s");
        final ObjectDataInput in = reader.getRawDataInput();
        dataList = new ArrayList<Data>(size);
        for (int i = 0; i < size; i++) {
            Data data = new Data();
            data.readData(in);
            dataList.add(data);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(name, ActionConstants.ACTION_ADD);
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{dataList};
    }
}
