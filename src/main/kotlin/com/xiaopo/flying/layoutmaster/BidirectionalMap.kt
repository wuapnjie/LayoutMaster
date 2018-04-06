package com.xiaopo.flying.layoutmaster

import java.util.LinkedHashMap

/**
 * @author wupanjie on 2018/4/5.
 */
class BidirectionalMap<K, V> {
  val keyToValueMap = LinkedHashMap<K, V>()
  val valueToKeyMap = LinkedHashMap<V, K>()

  fun put(k: K, v: V) {
    keyToValueMap[k] = v
    valueToKeyMap.remove(v)
    valueToKeyMap[v] = k
  }
}
