package org.petermac.yaml

/**
 * A helper class for constructing nested {@code Map}\/{@code List}
 * structures for building YAML\/JSON.
 *
 * Accessor keys are lists of strings and integers, with strings indexing
 * maps, and integers indexing lists. Where an element is not present,
 * an empty element of the appropriate type is created.
 *
 * For example, the code:
 *  def orl = new YamlComposer()
 *  for (Integer n = 0; n < 3; ++n) {
 *      orl[['ORL_O22','RESPONSE','PATIENT','ORDER',n,'ORC','Order Control']] = 'CR'
 *      orl[['ORL_O22','RESPONSE','PATIENT','ORDER',n,'ORC','Order Status']] = 'IP'
 *  }
 *  def yaml = new Yaml()
 *  println yaml.dump(orl.thing)
 *
 * yields:
 * ORL_O22:
 *   RESPONSE:
 *     PATIENT:
 *       ORDER:
 *       - ORC: {Order Control: CR, Order Status: IP}
 *       - ORC: {Order Control: CR, Order Status: IP}
 *       - ORC: {Order Control: CR, Order Status: IP}
 */
class YamlComposer {
    /**
     * The value being constructed.
     */
    public Object thing

    /**
     * Default constructor.
     * Creates an empty value.
     */
    YamlComposer() {
        thing = null
    }

    /**
     * Construct a {@code YamlComposer} with an initial
     * nested {@code Map}\/{@code List} object.
     *
     * @param x Value to initialize the composer object. Should be either a {@code Map} or a {@code List}
     */
    YamlComposer(Object x) {
        thing = x
    }

    /**
     * Index the object being composed.
     *
     * @param key A list of {@code String} and {@code Integer} objects to navigate a path down the nested structure.
     * @return The object at the position designated by the key.
     */
    Object getAt(List key) {
        thing = thing ?: makeFor(key[0])

        Object x = thing
        for (int i = 0; i < key.size() - 1; ++i) {
            def k = key[i]
            def k1 = key[i + 1]
            x = next(x, k, k1)
        }
        def k = key.last()
        return x[k]
    }

    /**
     * Set the value at a location in the nested structure. If an element of the
     * structure is not found, it is intialized with an empty {@code Map} or {@code List}
     * as appropriate for the following element of the key.
     *
     * @param key A list of {@code String} and {@code Integer} objects to navigate a path down the nested structure.
     * @param val A value to put at the designated place in the nested structure.
     */
    Object putAt(List key, Object val) {
        thing = thing ?: makeFor(key[0])

        Object x = thing
        for (int i = 0; i < key.size() - 1; ++i) {
            def k = key[i]
            def k1 = key[i + 1]
            x = next(x, k, k1)
        }
        def k = key.last()
        x[k] = val
        return x[k]
    }

    private Object next(Map m, String k, Object nk) {
        if (!m.containsKey(k)) {
            m[k] = makeFor(nk)
        }
        return m[k]
    }
    private Object next(List l, Integer j, Object nk) {
        if (l.size() == j) {
            l << makeFor(nk)
        }
        return l[j]
    }

    private Object makeFor(String key) {
        return [:]
    }
    private Object makeFor(Integer key) {
        return []
    }
}
