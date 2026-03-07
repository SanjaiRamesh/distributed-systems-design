# Caching in Distributed Systems

## Overview

Caching is one of the most common techniques used to improve read performance in distributed systems. It helps reduce latency, lower database load, and improve overall system responsiveness by serving frequently accessed data from memory instead of recomputing it or reading it from disk every time.

A well-designed caching strategy can make a system much faster, but it also introduces complexity around stale data, invalidation, failures, and uneven traffic patterns.

---

## Why Caching Matters

Databases are often the first bottleneck in read-heavy systems. Even a well-indexed database query is usually much slower than reading from memory. Because of that, caching is often introduced when:

- read traffic is very high
- the same data is requested repeatedly
- queries are expensive to compute
- API latency targets are stricter than database response times

Caching is especially useful when a small set of data is accessed repeatedly by many users.

---

## Where Caching Can Be Applied

Caching can exist at different layers of a system.

### 1. External Cache

This is the most common type in backend systems. A shared cache such as Redis sits between the application and the database and stores frequently read data.

**Use it when:**
- multiple application instances need access to the same cached data
- you want centralized eviction and expiration policies
- database reads are a scaling bottleneck

**Benefits:**
- shared across all app servers
- fast lookups
- supports TTL and eviction strategies

**Trade-off:**
- adds network hop and operational overhead

---

### 2. CDN Cache

A CDN caches content close to end users at edge locations.

**Use it for:**
- images
- videos
- static files
- cacheable public API responses

**Benefits:**
- reduces latency for geographically distributed users
- lowers origin server load

**Trade-off:**
- invalidation and freshness control can be harder for dynamic content

---

### 3. Client-Side Cache

Data may also be cached in browsers, mobile devices, or client SDKs.

**Examples:**
- browser HTTP cache
- local storage
- mobile offline storage
- client metadata caches

**Benefits:**
- avoids unnecessary network calls
- improves user experience

**Trade-off:**
- backend has limited control over freshness

---

### 4. In-Process Cache

An application can store hot data directly in local memory.

**Use it for:**
- configuration
- feature flags
- small reference data
- very hot keys
- precomputed results

**Benefits:**
- extremely fast
- no network round trip

**Trade-off:**
- not shared across instances
- harder to keep consistent across servers

---

## Common Cache Patterns

### 1. Cache-Aside

This is the most common and most practical cache pattern.

**Flow:**
1. Check cache
2. If hit, return data
3. If miss, read from database
4. Store result in cache
5. Return data

**Why it is popular:**
- simple
- efficient
- only caches data that is actually requested

**Trade-off:**
- cache miss increases response time for that request

**Best default choice** for most system design discussions.

---

### 2. Write-Through

In this pattern, writes go through the cache first, and the cache updates the database synchronously.

**Benefits:**
- cache stays fresh after writes

**Trade-offs:**
- slower writes
- more complex implementation
- risk of inconsistency if one write succeeds and the other fails

---

### 3. Write-Behind

Writes go to the cache first, and the cache writes to the database asynchronously later.

**Benefits:**
- very fast writes
- useful for high-volume ingestion

**Trade-offs:**
- risk of data loss if cache fails before flushing
- not suitable for critical financial correctness

**Good fit for:**
- analytics
- logs
- metrics pipelines

---

### 4. Read-Through

The cache acts as an intermediary and fetches data from the database on a miss.

**Benefits:**
- centralizes cache logic

**Trade-offs:**
- more specialized setup
- less common in normal backend services

CDNs are a common real-world example of read-through behavior.

---

## Eviction and Expiration

Because cache memory is limited, old entries must be removed over time.

### LRU
Least Recently Used removes data that has not been accessed recently.

**Good default** for many workloads.

### LFU
Least Frequently Used removes entries that are rarely accessed.

**Useful when** some keys remain popular for a long time.

### FIFO
First In First Out removes the oldest inserted items.

**Simple**, but usually not ideal for hot-key workloads.

### TTL
Time To Live sets an expiration time on cached entries.

TTL is especially useful when data should eventually refresh even if it is not explicitly invalidated.

In practice, TTL is often combined with LRU or LFU.

---

## Key Caching Problems

### 1. Cache Stampede

A cache stampede happens when a popular key expires and many requests miss the cache at the same time, all hitting the database together.

**Mitigations:**
- request coalescing / single-flight
- cache warming
- staggered TTLs
- probabilistic early refresh

---

### 2. Stale Data / Cache Consistency

If data is updated in the database but the cache still has the old value, users may see stale results.

**Mitigations:**
- invalidate cache on write
- use shorter TTLs
- accept eventual consistency for less critical data

There is no perfect answer. The strategy depends on how fresh the data must be.

---

### 3. Hot Keys

A hot key receives disproportionately high traffic and can overload one cache node or shard.

**Mitigations:**
- replicate hot keys
- add in-process caching for extremely hot values
- rate limit abusive patterns
- spread reads where possible

---

## How to Decide What to Cache

Good candidates for caching are usually:

- read frequently
- updated infrequently
- expensive to compute
- expensive to fetch from storage
- reused by many requests

Examples:
- user profiles
- product metadata
- feature flags
- homepage feed snapshots
- trending content
- session data
- reference configurations

Poor candidates:
- highly volatile data
- strongly consistent financial balances
- rarely accessed data
- very large objects with little reuse

---

## Suggested Interview Approach

When discussing caching in a system design interview, walk through it in this order:

1. Identify the bottleneck  
   Example: database reads are too slow or too expensive.

2. Explain what to cache  
   Choose high-read, low-change, high-cost data.

3. Choose the cache pattern  
   Cache-aside is usually the safest default.

4. Define eviction and TTL  
   LRU + TTL is often a strong default answer.

5. Address failure cases  
   Mention stale data, stampede, and hot keys.

This shows that you understand both the benefits and the operational risks.

---

## Practical Design Defaults

For many backend systems, a reasonable starting point is:

- **Redis** as external cache
- **Cache-aside** pattern
- **TTL** for freshness control
- **LRU** eviction
- **Invalidate on writes** for mutable records
- **Request coalescing** for hot or expensive keys
- **Small in-process cache** only for extremely hot values

---

## When Not to Cache

Caching should not be added automatically.

Avoid it when:
- the database is already fast enough
- data changes too frequently
- the cost of stale reads is too high
- the operational complexity is not justified

A good database design and indexing strategy may be enough for many systems.

---

## Summary

Caching improves performance by serving frequently requested data from memory instead of repeatedly hitting slower storage systems. It is one of the most effective ways to scale read-heavy systems, but it introduces trade-offs around consistency, invalidation, failure handling, and traffic imbalance.

The best practical default for most backend systems is:
- external cache
- cache-aside
- TTL
- clear invalidation strategy
- handling for stampede and hot keys

Understanding when to use caching is just as important as knowing how to implement it.