const med = (...arr) => {
  // Get index of middle element
  const mid = parseInt(arr.length / 2)
  // Sort to ascending
  const nums = arr.sort((a, b) => a - b)

  // If even elements return middle
  if (arr.length % 2 === 0) {
    return nums[mid - 1]
  } else {
    return (nums[mid] + nums[mid]) / 2
  }
}

const m = (a, b, c) => (!a & b & c) | (a & !b & c) | (a & b & !c) | (a & b & c)

const m5 = (a, b, c, d, e) => (!a & b & c & d & e) 

const m2 = (a, b, c, d) => (
  (!a & b & c & d) |
  (a & !b & c & d) |
  (a & b & !c & d) |
  (a & b & c & !d)
)

const medianGen = args => {
  // [[], [], []]
  let stuff = []
  for (let i = 0; i < args.length; i++) {
    // index: i (0, 1, 2) === 
    let arr = args

    arr[i] = (!arr[i]) ? 1 : 0

    stuff.push(arr)
  }
  return stuff
}

/* console.log(med(0, 1, 0, 1, 0, 1))
console.log(med(0, 1, 1, 0, 0, 0, 1)) */
medianGen([0, 1, 1])