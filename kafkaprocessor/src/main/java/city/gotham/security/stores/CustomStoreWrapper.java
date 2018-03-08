package city.gotham.security.stores;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CustomStoreWrapper<K, V> implements KeyValueStore<K, V> {

    private KeyValueStore<K, V> store;
    private Map<K, V> alternativeStore;

    public CustomStoreWrapper() {
        alternativeStore = new Hashtable<>();
    }

    public CustomStoreWrapper(KeyValueStore<K, V> store) {
        this.store = store;
    }

    public void clear() {
        alternativeStore.clear();
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
        return keyValueStoreWasSet() ?
                store.putIfAbsent(key, value) :
                alternativeStore.putIfAbsent(key, value);
    }

    @Override
    public void putAll(List<KeyValue<K, V>> entries) {
        if (keyValueStoreWasSet()) {
            store.putAll(entries);
        } else {
            for (KeyValue<K, V> entry : entries) {
                alternativeStore.put(entry.key, entry.value);
            }
        }
    }

    @Override
    public V delete(K key) {
        return keyValueStoreWasSet() ? store.delete(key) : alternativeStore.remove(key);
    }

    @Override
    public String name() {
        return keyValueStoreWasSet() ? store.name() : null;
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
        return keyValueStoreWasSet() && store.persistent();
    }

    @Override
    public boolean isOpen() {
        return keyValueStoreWasSet() && store.isOpen();
    }

    @Override
    public V get(K key) {
        return keyValueStoreWasSet() ? store.get(key) : alternativeStore.get(key);
    }

    @Override
    public KeyValueIterator<K, V> range(K from, K to) {
        return keyValueStoreWasSet() ? store.range(from, to) : null;
    }

    @Override
    public KeyValueIterator<K, V> all() {
        return keyValueStoreWasSet() ? store.all() : null;
    }

    @Override
    public long approximateNumEntries() {
        return keyValueStoreWasSet() ? store.approximateNumEntries() : 0;
    }

    private boolean keyValueStoreWasSet() {
        return store != null;
    }

}
