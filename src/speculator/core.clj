(ns speculator.core
  (require [clojure.string :as s]
           [twitter.oauth :refer :all]
           [twitter.request :refer :all]
           [twitter.api.restful :refer :all])
  (:gen-class))

(def nouns (s/split (slurp "resources/nouns.txt") #"\n"))
(def adjectives (s/split (slurp "resources/adjectives.txt") #"\n"))
(def beginnings ["I bet "
                 "I bet "
                 "What if "
                 "What if "
                 "Maybe "
                 "I think "
                 "Do you think "
                 "Sometimes "
                 "Some "
                 "Perhaps "
                 "I wonder if "])

(defn nth
  "Like clojure.core/nth, but handles negative indices correctly"
  ([coll index]
     (clojure.core/nth coll
                       (if (neg? index)
                         (+ (count coll) index)
                         index)))
  ([coll index not-found]
     (clojure.core/nth coll
                       (if (neg? index)
                         (+ (count coll) index)
                         index)
                       not-found)))

(defn pluralize
  "Pluralize a noun, assuming it follows regular pluralization rules"
  [noun]
  (let [z (nth noun -1)
        y (nth noun -2)]
    (if (or (contains? #{\s \x \z} z)
            (= [y z] [\c \h])
            (= [y z] [\s \h]))
      (str noun "es")
      (str noun "s"))))

(defn speculate
  []
  (let [beginning (rand-nth beginnings)
        [n1 n2] (take 2 (repeatedly #(pluralize (rand-nth nouns))))
        a (rand-nth adjectives)]
    (str beginning n1 " are just " a " " n2)))

(defn read-auth-config
  "The auth config file must be a newline-separated file containing your
  app-key, app-secret, user-key, and user-secret, respectively."
  [config-path]
  (let [config (s/split (slurp config-path) #"\n")]
    (apply make-oauth-creds config)))

(defn post-speculation!
  [creds]
  (statuses-update :oauth-creds creds :params {:status (speculate)}))

(defn -main
  [config-path]
  (let [auth (read-auth-config config-path)]
    (println (post-speculation! auth))))
