(ns demo
  #_{:clj-kondo/ignore [:unused-referred-var]}
  (:require [clojure.repl :refer [doc source]]
            [clojure.pprint :refer [pprint]]
            [clojure.java.javadoc :refer [javadoc]]))

;; an infinite sequence...
(def nums (range))

;; let's take a look at 30 of them:
(def first-thirty (take 30 nums))

;; side effects!
(println first-thirty)

;; let's make a vector (array/list)
(comment ; nothing in this comment block will evaluate unless I choose to do so
  ;; (source into) -- uncomment to see the source code for clojure.core/into
  ;; (doc into) -- uncomment to print its doc string
  (let [v1 (into [] first-thirty) ; into "pours" one data structure into another 
        v2 (vec first-thirty) ; we can also use `vec`
        modified-v2 (assoc v2 0 13)]
    #_{:clj-kondo/ignore [:unused-value]}
    (= v2 modified-v2) ;; if vectors were mutable, we would expect v2 to change!
    (source =)
    ; (javadoc Object)
    (= v1 v2)
    ;; notice that evaluating this let block shows the results in this buffer, unlike calling `println`
    ;; (take 10 nums) ;; uncomment this to see that the original sequence is untouched
    ;; moreover, the last value or expression in the let block will be returned
    ))

;; threading macros/control flow
;; functional programming languages like Clojure often have expressive control flow operators
(if true 42 24)
(when true 42) ; like if without an else
;; but what if you want the inverse?
(if-not false 42 24) ; tada!
(when-not false 42)
;; we saw `let` above
;; there are other functions and special forms that have a similar binding structure
;; let's see a few
(if-some [last-num (get (vec (range 10)) 11)]
  last-num
  11)
(when-some [last-num? (get (vec (range 10)) 11)]
  last-num?)
;; cond is sort of like many if-else statements
(let [some-more-nums (range 42 100)
      a-value (cond ;; but it's an expression, and thus assignable
                (> (count some-more-nums) 1000) "That's a lot of numbers"
                :else (format "More like: %d numbers" (count some-more-nums)))]
  (macroexpand ;; here's how it looks "unthreaded"
   `(->> ~a-value ;; here we are making use of the "thread last" macro to create a pipeline of functions
         (re-seq #"\d+") ; re-seq returns a sequence of regex matches
         first ; first extracts the first element from that sequence
         parse-long)))

;; there's also some-> and some->>
;; these are like the ? operator in JS
(def -empty [])

(some-> -empty first println)

;; levelling up...
(->> nums
     (take 30)
     (map inc)
     (filter odd?)
     (into []))

;; the core abstraction -- functions!
(defn inc-dirty [num]
  ;; Clojure is not a pure functional language -- functions can have side effects
  (println num)
  (inc num))

;; to build abstractions, we can compose functions
;; we've seen something similar with the threading macros already
;; but there's also `comp`
(def pipeline (comp (take 30) (map inc-dirty) (filter odd?)))
(comment (type pipeline))
(into [] pipeline nums) ;; with into our pipeline runs left to right, as with the threading macro

;; partial application
(doc partial)

(defn fizzbuzz [n]
  (let [mod-n (partial mod n)]
    (cond
      (and (zero? (mod-n 5))
           (zero? (mod-n 3))) "fizzbuzz|"
      (zero? (mod-n 3)) "fizz|"
      (zero? (mod-n 5)) "buzz|"
      :else (str n "|"))))

;; TABLES
(def foobar (into [] (comp
                      (take 100)
                      (map fizzbuzz)
                      ; (interpose "|")
                      (partition-all 5)) (range 1 Long/MAX_VALUE)))

(pprint foobar) ;; pretty printed "table"

;; let's flip the columns and rows of this table
;; so it's shape is 9x5 instead of 5x9
(pprint (apply map vector foobar))

; (doc apply)
;; function application is a core concept in programming
;; it's used in OOP and imperative languages too, but more behind the scenes
;; in this example, `map` is being applied to its arguments
;; but map is a higher-order function, so the function `vector` is also one of its arguments!
;; crazy, I know. Let's see a simpler example of `apply`
#_{:clj-kondo/ignore [:type-mismatch]}
(max (range 10)) ; this doesn't work
(apply max (range 10)) ; but this does!
; the same code as above in JS: Math.max.apply(null, [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])

;; back to basics for a minute
(vector [1 2 3]) ; => [[1 2 3]]
(apply vector [1 2 3]) ; => [1 2 3]

;; foobar is a vector containing other vectors, so
;; by applying `(map vector)` to it, we get the effect of "zipping"
;; i.e., the first element of each inner vector is put into the first output, the second element
;; into the second, and so on.
;; in other languages that have `map`, they typically only take one collection as an argument
;; Clojure's map is "lifted" (fancy FP term to be passed over in silence) -- it takes many
(map + [1 2 3] [4 5 6] [7 8 9]) ; here demonstrated by summing the columns

;; let's meet `mapcat`
;; like flatMap/flat_map in other languages
;; a common example first
(mapcat (fn [n] [n n]) (range 10))

(->> foobar ;; our original table 
     (mapcat (fn [coll] (conj coll "\n"))) ; adding some newlines for readability
     (apply str) ;; stringified 
     (spit "foobar.csv")
     )

;; the classic `reduce` example (sums)
(reduce + (range 10)) ; without accumulator - uses the first value in the input collection
(reduce + 0 (range 10)) ; with accumulator - same result
;; TODO: demonstrate reduce with non-trivial example
;; implementing map and filter in terms of reduce
(defn my-map [f coll]
  (reduce (fn [acc item] (conj acc (f item))) [] coll))

(defn my-filter [pred coll]
  (reduce (fn [acc item] (if (pred item)
                           (conj acc item)
                           acc)) [] coll))

(my-map inc (range 10))
(my-filter even? (range 10))

;;;;;;;;;;;;;;;;;;;;;;;;;BONUS;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                     ;;
;;                                                     ;;
;;                                                     ;;
;;                                                     ;;
;;                                                     ;;
;;                                                     ;;
;;                                                     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;