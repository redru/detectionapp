package city.gotham.security.stores;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomStoreWrapper<K, V> implements KeyValueStore<K, V> {

    private KeyValueStore<K, V> store;
    private Map<K, V> alternativeStore;

    public CustomStoreWrapper() {
        alternativeStore = new HashMap<>();
    }

    public CustomStoreWrapper(KeyValueStore<K, V> store) {
        this.store = store;
    }

    @Override
    public void put(K key, V value) {
        if (keyValueStoreWasSet()) {
            store.put(key, value);
        } else {
            alternativeStore.put(key, value);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (keyValueStoreWasSet()) {
            store.put(key, value);
        }

        return value;
    }

    @Override
    public void putAll(List<KeyValue<K, V>> entries) {
        if (keyValueStoreWasSet()) {
            store.putAll(entries);
        }
    }

    @Override
    public V delete(K key) {
        if (keyValueStoreWasSet()) {
            return store.delete(key);
        } else {
            return alternativeStore.remove(key);
        }
    }

    @Override
    public String name() {
        if (keyValueStoreWasSet()) {
            return store.name();
        }

        return null;
    }

    @Override
    public void init(ProcessorContext context, StateStore root) {
        if (keyValueStoreWasSet()) {
            store.init(context, root);
        }
    }

    @Override
    public void flush() {
        if (keyValueStoreWasSet()) {
            store.flush();
        }
    }

    @Override
    public void close() {
        if (keyValueStoreWasSet()) {
            store.close();
        }
    }

    @Override
    public boolean persistent() {
        if (keyValueStoreWasSet()) {
            return store.persistent();
        }

        return false;
    }

    @Override
    public boolean isOpen() {
        if (keyValueStoreWasSet()) {
            return store.isOpen();
        }

        return false;
    }

    @Override
    public V get(K key) {
        if (keyValueStoreWasSet()) {
            return store.get(key);
        } else {
            return alternativeStore.get(key);
        }
    }

    @Override
    public KeyValueIterator<K, V> range(K from, K to) {
        if (keyValueStoreWasSet()) {
            return store.range(from, to);
        }

        return null;
    }

    @Override
    public KeyValueIterator<K, V> all() {
        if (keyValueStoreWasSet()) {
            return store.all();
        }

        return null;
    }

    @Override
    public long approximateNumEntries() {
        if (keyValueStoreWasSet()) {
            return store.approximateNumEntries();
        }

        return 0;
    }

    private boolean keyValueStoreWasSet() {
        return store != null;
    }

}
