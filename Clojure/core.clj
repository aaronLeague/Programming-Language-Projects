;; Author: Aaron B. League
;; Created 2019.03.03

(ns nandparser.core)

(defn io-match [outer]
  ;;are there any inner nand/outer nand matches?
  (some true?
        ;;are there any nested brackets?
        (for [x (filter seq? outer)]
          ;;if so, check each one to see if its contents
          ;;are present in the outer expression
          (reduce #(and %1 %2)
                  (for [y x]
                    (some (fn [i] (= i y)) outer))))))

(defn simplify [l]
  (cond
    ;;for (nand (nand x)), return x
    (and (= (count l) 2)
         (seq? (nth l 1))
         (= (count (nth l 1)) 2))
    (nth (nth l 1) 1)
    ;;if an inner nand matches outer nand data, return true
    (io-match l)
    true
    ;;If none of these special cases apply, just return the expression as-is
    :default
    l
    ))

;;simplify nand expressions
(defn un-nand-it [l]
  (cond
    ;;Say something if the syntax is wrong
    (not= (first l) 'nand) (println "Not a NAND statment!")
    ;;return true if false is in the expression
    (some false? l) true
    ;;return false if true is the only value in the expression
    (not (some #(not= % true) (drop 1 l))) false
    :default (simplify
               (distinct
                 (remove #(= % true) l)))
    ))

;;recursively simplify nand expressions
(defn deep-un-nand [l]
  (un-nand-it
    (map #(if (seq? %)
            (deep-un-nand %)
            %)
         l)))

;;Taken from class code: Schlegel, CSC344, Spring 2019, SUNY Oswego
(defn substitute
  [m l]
  (map #(if (seq? %)
          (substitute m %)
          (get m % %))
       l))

;;map everything to nand
(def nand-map '{not nand and nand or nand})

;;convert and expressions to nand expressions
(defn and-to-nand [l]
  (list 'nand (substitute nand-map l)))

;;convert or expressions to nand expressions
(defn or-to-nand [l]
  (map #(get nand-map
             %
             (list 'nand %))
       l))

;;convert logical expressions to nand form
(defn nand-convert [l]
  (cond
    (and (= (first l) 'not)
         (= (count l) 2))
    (substitute nand-map l)
    (= (first l) 'and)
    (and-to-nand l)
    (= (first l) 'or)
    (or-to-nand l)
    :default
    (println "UNRECOGNIZED LOGICAL EXPRESSION!")))

(defn deep-convert [l]
  (nand-convert
    (map #(if (seq? %)
            (deep-convert %)
            %)
         l)))

;;entry point for the software
(defn evalexp [exp bind]
  (deep-un-nand (deep-convert (substitute bind exp))))
