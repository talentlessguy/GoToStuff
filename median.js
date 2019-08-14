const med = (x, y, z) => {
  return (x & y) || (x & z) || (y & z)
}

const nums = [
  [0, 1, 1],
  [1, 1, 1],
  [1, 0, 0],
  [0, 0, 0],
  [0, 1, 0],
  [1, 1, 0]
]

for (let i of nums) {
  console.log(med(i[0], [1]))
}