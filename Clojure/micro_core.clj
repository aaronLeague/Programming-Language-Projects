;; Author: Aaron B. League
;; Created 2019.03.03

(ns nandparser.core)

;;works for true and false only, not variables or '()
;(defn nand [& m] (not (reduce #(and %1 %2) m)))

;;works
;(defn remove-it [l a]
;  (remove #(= % a) l))

;;works
;(defn is-all-true [m] (not (some #(not= % true) m)))

;;works
;(defn remove-true [l]
;  (distinct (remove #(= % true) l)))

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

(defn simplify [list]
  (cond
    ;;for (nand (nand x)), return x
    (and (= (count list) 2)
         (seq? (nth list 1))
         (= (count (nth list 1)) 2))
    (nth (nth list 1) 1)
    ;;if an inner nand matches outer nand data, return true
    (io-match list)
    true
    ;;If none of these special cases apply, just return the expression as-is
    :default
    list
    ))

;;works so far
(defn un-nand-it [list]
  (cond
    ;;Say something if the syntax is wrong
    (not= (first list) 'nand) (println "Not a NAND statment!")
    ;;return true if false is in the expression
    (some false? list) true
    ;;return false if true is the only value in the expression
    (not (some #(not= % true) (drop 1 list))) false
    :default (simplify
               (distinct
                 (remove #(= % true) list)))
    ))

;;verify program with a list of test cases
(for [x '((nand false)
           (nand true)
           (nand (nand x))
           (nand x (nand x))
           (nand x x)
           (nand x y)
           (nand x true)
           (nand x false)
           (nand true true)
           (nand x y true)
           (nand x true true)
           (nand true true true)
           (nand x y false)
           (nand x y z)
           (x y z)
           (nand (nand (nand x)))
           (nand (nand (nand (nand x)))))]
  (println (un-nand-it x)))
