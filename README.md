# distributed-systems-design
Hands-on system design implementations: rate limiter, URL shortener, distributed cache, load balancing &amp; scalability patterns.

1) Requirements
2) Core Entities
3) API or interface
4) Data flow
5) High-level design
6) Deep dive

### Requirements

What is url shorter?
#### Functional Requirements:(what user can do)
 - User can create a short URL for a given long URL.
   - custom short alias for the long URL. 
   - optionally, user can set an expiration time for the short URL.
 - User can access the original long URL using the short URL.

#### Non-Functional Requirements:(how the system should perform)
 - low latency on URL redirection(~200ms).
 - scale to support 100M dau and 1BURLS
 - ensure uniqueness of short code
 - High availability >> eventual Consistency for url shortneing
#### BOTE
 - scale
 - latency
 - storage

### Core Entities
 - Original URL: The long URL that users want to shorten.
 - Short URL: The generated short URL that maps to the original URL.
 - User: The individual or entity that creates and manages short URLs.

### API or interface
#### Shorten a url
 -  POST /urls
````
Request Body:{
Originalurl: string,
CustomAlias: string (optional),
ExpirationTime: datetime (optional)
}

````
Url Table:
 - short url/customAlias(pk)-automatically build index
 - long/original url
 - expirationTime
 - createDatetime
 - userId


#### Redirect to original url
 - GET /{shortUrl} --> redirect to original url
 - 302 redirect temporary, never going to cache(preferred)
 - 301 redirect permanent(request will not come to server so we will save compute but cannot track, will loos visibility of traffic)
````

### Deep dive
 - fast
 - unique
 - sort(5 to 7 chars)


1) prefix of the long url ex www.bitly{shot}.com
2) random number generator 10^9 10 character
 -  base62 encoding (0-9, a-z, A-Z) to generate short code
 - 62^6 = 56.8B unique combinations
 - birthday paradox
 - 880k collisions
 -  we just need to check for collision first
3) hash the long url
    - md5(longUrl) -> hash->base62(hash) 
    md5(longUrl) or sha256
    - 128 or 256 bits
    - base62 encoding to get short code
    - collision is possible but very low probability
4) counter
 - increment a global counter for each new URL and encode the counter value to base62 to get the short code
 -  predictability which is bad for security.
   - "waning" don't shorten private urls"
   - rate limiting
   
 - bijective function
  - sqids.org
  

#### Non-Functional Requirements:
 - add index on short url for fast lookup(PK itself fine, if need we can add hash indexing).
 - cache popular urls in memory (redis) to reduce latency and database load.
  - key: shortCode, value: long Url
   