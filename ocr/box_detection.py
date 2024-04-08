from boxdetect import config
from boxdetect.pipelines import get_boxes
import matplotlib.pyplot as plt
import cv2
import os

cfg = config.PipelinesConfig()

# important to adjust these values to match the size of boxes on your image
# cfg.width_range = (110, 130)
# cfg.height_range = (110, 130)

cfg.width_range = (80, 170)
cfg.height_range = (80, 170)

# the more scaling factors the more accurate the results but also it takes more time to processing
# too small scaling factor may cause false positives
# too big scaling factor will take a lot of processing time
cfg.scaling_factors = [0.7]

# w/h ratio range for boxes/rectangles filtering
cfg.wh_ratio_range = (0.9, 1.1)

# group_size_range starting from 2 will skip all the groups
# with a single box detected inside (like checkboxes)
cfg.group_size_range = (2, 100)

# num of iterations when running dilation tranformation (to engance the image)
cfg.dilation_iterations = 0


# Specify the directory path
directory = "input_img"

for file_name in os.listdir(directory):
    file_path = os.path.join(directory, file_name)
    if os.path.isfile(file_path) and file_path.endswith(".jpg"):
        rects, grouping_rects, image, output_image = get_boxes(
            file_path, cfg=cfg, plot=False
        )

        output_img_path = os.path.join("output_img", file_name)
        cv2.imwrite(output_img_path, output_image)

        output_txt_path = (
            os.path.join("output_txt", file_name.removesuffix(".jpg")) + ".txt"
        )

        with open(output_txt_path, "w") as file:
            for rect in rects:
                file.write(" ".join(map(str, rect)) + "\n")


# plt.figure(figsize=(20, 20))
# plt.imshow(output_image)
# plt.show()
