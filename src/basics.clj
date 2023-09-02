(ns basics
  (:require [clojure.walk :refer [macroexpand-all]]
            [clojure.pprint :refer [pprint]]))

;; CODE IS DATA
(type (+ 1 1))
(type '(+ 1 1)) ; that tickmark is a quote -- it means "don't evaluate"

;; everything in the first position of such a list, when evaluated
;; is assumed by the runtime to be a function
;; enforced by the clojure.lang.IFn interface
(comment
  (instance? clojure.lang.IFn +)
  (instance? clojure.lang.IFn 1)
  ; (1 + 1) ;; throws ClassCastException
  )

;; special forms and macros
;; The entire Clojure language is composed from a *very* small set of primitives
;; this follows in the Lisp tradition
;; special forms, like `let`, don't adhere to the above contract
(let [my-name "Ben"]
  (println my-name))

;; let is a macro as well: it is expanded by the compiler into other code
;; let's see
(macroexpand-all '(let [my-name "Ben"]
                    (println my-name)))

;; that's not too exciting
;; check out `doseq` though, Clojure's form of a side-effecting loop construct
(pprint (macroexpand '(doseq [x (range 10)]
                        (println x))))

;; basic data structures
;; besides lists, there's maps and vectors. THAT'S IT!!

;; lists are primarily for evaluation
(+ 1 1)

;; vectors are for grouping of data
[1 2 3]
;; or, as above, for binding the results of expressions to local variables
(let [my-name "Ben"] ;; in this block, `my-name` is bound to "Ben"
  (println my-name) ;; pretty standard fare
  )

;; maps are for associative relationships
;; like objects and dictionaries in other languages
{:my-name "Ben"} ;; we won't cover these too much

;; but here's a cool feature of maps and vectors
;; they are functions!
([1 2 3] 1)
({:my-name "Ben"} :my-name)

;; maps can also have keys that are not "hashable" in other languages
(get {[:a :key] :a-value} [:a :key])

;; I snuck keywords by you above
;; they are unique values that always evaluate to themselves
;; they are like strings with some extra powers, namely
;; that they can be used as functions
(:my-name {:my-name "Ben"})

;; miscellania
(def my-name "Ben") ; creates a "Var"

;; defn creates a function
;; but it's also a macro
;; (macroexpand '(defn a-fn [] (println "I'm a function")))
(defn yell-my-name [name]
  ;; we just used a Java method `.toUpperCase`
  ;; Java methods are distinguished by that dot in front of the function name
  (.toUpperCase name))

(yell-my-name my-name)