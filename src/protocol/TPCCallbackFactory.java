package protocol;

import TPC.TPCConnectionHandler;

public interface TPCCallbackFactory<T> {
	ProtocolCallback<T> create(TPCConnectionHandler<T> handler);

}
