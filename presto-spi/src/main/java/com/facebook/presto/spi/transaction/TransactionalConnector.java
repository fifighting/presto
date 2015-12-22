/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.spi.transaction;

import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.ConnectorIndexResolver;
import com.facebook.presto.spi.ConnectorPageSinkProvider;
import com.facebook.presto.spi.ConnectorRecordSinkProvider;
import com.facebook.presto.spi.SystemTable;
import com.facebook.presto.spi.TransactionalConnectorPageSourceProvider;
import com.facebook.presto.spi.TransactionalConnectorRecordSetProvider;
import com.facebook.presto.spi.TransactionalConnectorSplitManager;
import com.facebook.presto.spi.security.TransactionalConnectorAccessControl;
import com.facebook.presto.spi.session.PropertyMetadata;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public interface TransactionalConnector
{
    ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly);

    ConnectorHandleResolver getHandleResolver();

    /**
     * Guaranteed to be called at most once per transaction.
     */
    TransactionalConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle);

    TransactionalConnectorSplitManager getSplitManager();

    /**
     * @throws UnsupportedOperationException if this connector does not support reading tables page at a time
     */
    default TransactionalConnectorPageSourceProvider getPageSourceProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support reading tables record at a time
     */
    default TransactionalConnectorRecordSetProvider getRecordSetProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support writing tables page at a time
     */
    default ConnectorPageSinkProvider getPageSinkProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support writing tables record at a time
     */
    default ConnectorRecordSinkProvider getRecordSinkProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support indexes
     */
    default ConnectorIndexResolver getIndexResolver()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the set of system tables provided by this connector
     */
    default Set<SystemTable> getSystemTables()
    {
        return emptySet();
    }

    /**
     * @return the system properties for this connector
     */
    default List<PropertyMetadata<?>> getSessionProperties()
    {
        return emptyList();
    }

    /**
     * @return the table properties for this connector
     */
    default List<PropertyMetadata<?>> getTableProperties()
    {
        return emptyList();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not have an access control
     */
    default TransactionalConnectorAccessControl getAccessControl()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Commit the transaction. Will be called at most once and will not be called if abort is called.
     */
    default void commit(ConnectorTransactionHandle transactionHandle)
    {
    }

    /**
     * Abort the transaction. Will be called at most once and will not be called if commit is called.
     */
    default void abort(ConnectorTransactionHandle transactionHandle)
    {
    }

    /**
     * True if the connector only supports write statements in independent transactions.
     */
    default boolean isSingleStatementWritesOnly()
    {
        return false;
    }

    /**
     * Shutdown the connector by releasing any held resources such as
     * threads, sockets, etc. This method will only be called when no
     * queries are using the connector. After this method is called,
     * no methods will be called on the connector or any objects that
     * have been returned from the connector.
     */
    default void shutdown() {}
}