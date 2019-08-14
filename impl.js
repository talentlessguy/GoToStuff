const implication = (a, b) => {
  return (!a & !b) || (a & b) || (!a & b)
}

const revImplication = (a, b) => {
  return (a & !b) || (!b & a) || (a & !b)
}

const nums = [
  [0, 1],
  [1, 1],
  [1, 0],
  [0, 0]
]

console.log('Implication')

nums.map(n => console.log(implication(n[0], n[1])))

console.log('Reverse implication')

nums.map(n => console.log(revImplication(n[0], n[1])))