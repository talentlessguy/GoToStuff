const func = ([a, b, c, d, e, f]) => {
  return (!a & b) || (c & d) || (!e & f)
}

const cases = [
  [0, 1, 1, 1, 0, 1],
  [0, 0, 1, 0, 0, 0],
  [0, 0, 0, 0, 1, 0],
  [1, 1, 1, 1, 1, 1],
  [1, 0, 1, 0, 0, 1]
]

for (let i of cases) {
  console.log(func(i))
}

