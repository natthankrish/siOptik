import cv2
import numpy as np
from img_io import display_image, resize_image

def check_border_center(arr, pt, w, h):
  # Used to filter points to get 4 distinct border center
  threshold = 50
  sum = pt[0] + pt[1]
  for arr_pt in arr:
    temp_sum = arr_pt[0] + arr_pt[1]
    if (abs(temp_sum - sum) < threshold):
      return False
  return True

def detect_border(img, border):
  # Minimum similarity 0.8
  threshold = 0.8
  iteration = 8
  # Exponential decremental
  # Search for each scale of border
  for i in range(0, iteration):
    scale = 1 - (1/iteration) * i
    temp_border = resize_image(border, scale)
    w, h = temp_border.shape[:2]
    res = cv2.matchTemplate(img, temp_border, cv2.TM_CCOEFF_NORMED)
    loc = np.where(res >= threshold)

    # stop if boxes are found
    if (len(loc[0]) > 0 and len(loc[1]) > 0):

      border_center_arr = []
      for pt in zip(*loc[::-1]):
        # cv2.rectangle(image, start_point, end_point, color, thickness) 
        cv2.rectangle(img, pt, (pt[0] + w, pt[1] + h), (0,0,255), 2)

        w_center = (pt[0] + (pt[0] + w)) /2
        h_center = (pt[1] + (pt[1] + h)) /2
        p = (w_center, h_center)

        if (len(border_center_arr) == 0):
          border_center_arr.append(p)
        else:
          if (check_border_center(border_center_arr, p, w, h)):
            border_center_arr.append(p)
        
      return img, border_center_arr
  
  return None, None

  


  