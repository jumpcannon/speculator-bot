(ns speculator.core
  (require [clojure.string :as s]
           [clojure.pprint :as pp]
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

(defn read-oauth-creds
  "The auth config file must be a newline-separated file containing your
  app-key, app-secret, user-key, and user-secret, respectively."
  [path]
  (let [config (s/split (slurp path) #"\n")]
    (apply tw-oauth/make-oauth-creds config)))

(defn tweet!
  [creds]
  (let [text (speculate)]
    (tw-api/statuses-update :oauth-creds creds :params {:status text})))

(defn post!
  [options]
  (-> options
      :oauth
      read-oauth-creds
      tweet!
      :body
      (select-keys [:id_str :text])))

(defn delete!
  [creds id_str]
  nil
  )


(def cli-options
  [["-o" "--oauth FILE" "Location of oauth credential file"]
   ["-n" "--no-tweet N" "Don't tweet anything, just run the generator N times and print the results to the console (for development/testing)"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "Must be a number greater than 0"]]
   ["-d" "--delete" "emulate a normal run all the way through posting a tweet, but delete it immediately after confirming it was posted successfully (for development/testing)"]
   ["-h" "--help" "print this help message"]])

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      (every? options [:oauth :delete]) (-> options post! delete! pp/pprint)
      (:oauth options) (-> options post! pp/pprint)
      :else (dotimes [_ (:no-tweet options)]
              (println (speculate))))))
