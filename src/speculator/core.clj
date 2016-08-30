(ns speculator.core
  (require [clojure.string :as s]
           [clojure.tools.cli :as cli]
           [twitter.oauth :as tw-oauth]
           [twitter.api.restful :as tw-api])
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

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn nnth
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
  (let [z (nnth noun -1)
        y (nnth noun -2)]
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
    (apply tw-oauth/make-oauth-creds config)))

(defn post-speculation!
  [creds]
  (tw-api/statuses-update :oauth-creds creds :params {:status (speculate)}))

(def cli-options
  [["-d" "--dry-run" "Just print to console, don't tweet"]
   ["-o" "--oauth FILE" "Location of oauth credential file"]
   ["-h" "--help"]])

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      (:dry-run options) (println (speculate))
      (:oauth options) (post-speculation! (:oauth options))
      :else (println summary))))
