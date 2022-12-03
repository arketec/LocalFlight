package dev.arketec.localflight.util;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class TimeoutList<V> implements Iterable<V> {

    private final TimeoutDelegate<V> delegate;
    private final EnumSet<TickEvent.Type> tickTypes;

    private final List<TimeoutEntry<V>> tickEntries = new LinkedList<>();

    public TimeoutList(@Nullable TimeoutDelegate<V> delegate, TickEvent.Type... types) {
        this.delegate = delegate;
        this.tickTypes = EnumSet.noneOf(TickEvent.Type.class);
        for (TickEvent.Type type : types) {
            if (type != null) {
                this.tickTypes.add(type);
            }
        }
    }

    public void add(V value) {
        this.add(0, value);
    }

    public void add(int timeout, V value) {
        if (value == null) return;

        this.tickEntries.add(new TimeoutEntry<>(timeout, value));
    }

    public boolean setTimeout(int timeout, @Nonnull V value) {
        for (TimeoutEntry<V> entry : tickEntries) {
            if (entry.value.equals(value)) {
                entry.timeout = timeout;
                return true;
            }
        }
        return false;
    }

    public boolean setOrAddTimeout(int timeout, @Nonnull V value) {
        if (!contains(value)) {
            add(timeout, value);
            return true;
        } else {
            return setTimeout(timeout, value);
        }
    }

    public boolean contains(V value) {
        if (value == null) return false;
        for (TimeoutEntry<V> element : tickEntries) {
            if (element.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(V key) {
        return this.removeIf(key::equals);
    }

    public boolean removeIf(Predicate<V> test) {
        return this.tickEntries.removeIf(e -> test.test(e.value));
    }

    public int getTimeout(V value) {
        for (TimeoutEntry<V> entry : tickEntries) {
            if (entry.value.equals(value)) {
                return entry.timeout;
            }
        }
        return -1;
    }

    public void addAll(TimeoutList<V> entries) {
        if (entries == null) return;

        for (TimeoutEntry<V> entry : entries.tickEntries) {
            setOrAddTimeout(entry.timeout, entry.value);
        }
    }

    public boolean isEmpty() {
        return tickEntries.isEmpty();
    }

    public void tick(TickEvent.Type type) {
        Iterator<TimeoutEntry<V>> iterator = tickEntries.iterator();
        while (iterator.hasNext()) {
            TimeoutEntry<V> entry = iterator.next();
            entry.timeout--;
            if (entry.timeout <= 0) {
                if (delegate != null) {
                    delegate.onTimeout(entry.value);
                }
                iterator.remove();
            }
        }
    }

    public void tickEntry(V entryIn) {
        Iterator<TimeoutEntry<V>> iterator = tickEntries.iterator();
        while (iterator.hasNext()) {
            TimeoutEntry<V> entry = iterator.next();
            if (entry.value.equals(entryIn)) {
                entry.timeout--;
                if (entry.timeout <= 0) {
                    if (delegate != null) {
                        delegate.onTimeout(entry.value);
                    }
                    iterator.remove();
                }
            }
        }
    }

    public void clear() {
        if (delegate != null) {
            tickEntries.forEach(entry -> {
                delegate.onTimeout(entry.value);
            });
        }
        tickEntries.clear();
    }

    @Override
    public Iterator<V> iterator() {
        Iterator<TimeoutEntry<V>> entryIterator = tickEntries.iterator();
        return new Iterator<V>() {

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public V next() {
                return entryIterator.next().value;
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        };
    }

    public EnumSet<TickEvent.Type> getHandledTypes() {
        return tickTypes;
    }

    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    public String getName() {
        return "TimeoutList";
    }

    public static interface TimeoutDelegate<V> {

        public void onTimeout(V object);

    }

    private static class TimeoutEntry<V> {

        @Nonnull
        private final V value;
        private int timeout;

        private TimeoutEntry(int timeout, @Nonnull V value) {
            this.timeout = timeout;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeoutEntry that = (TimeoutEntry) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

}

