#lang racket

(struct point (x y z) #:transparent)

(define (scalar p1 p2)
  (+ 
    (* (point-x p1) (point-x p2))
    (* (point-y p1) (point-y p2))
    (* (point-z p1) (point-z p2))
  )
)

(define (middle p1 p2)
  (point 
    (/ (+ (point-x p1) (point-y p2)) 2)
    (/ (+ (point-y p1) (point-y p2)) 2)
    (/ (+ (point-z p1) (point-z p2)) 2)
  )
)

(scalar (point 1 1 1) (point 1 2 3))
(middle (point 1 1 1) (point 1 2 3))