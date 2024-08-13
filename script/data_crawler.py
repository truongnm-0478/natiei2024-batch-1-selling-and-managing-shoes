import requests
import random
import json
from bs4 import BeautifulSoup

url = "https://ananas.vn/wp-admin/admin-ajax.php?action=filterFollow&gender=men%2Cwomen&category=shoes&attribute=&relation=and&paged"

current_page = 1
response = requests.get(f'{url}={current_page}')
json_data = response.json()

# table data rows
constant_rows = []
product_rows = []
product_detail_rows = []
product_image_rows = []
product_quantity_rows = []

def get_constants():
    global constant_rows, json_data
    for value in ["35", "36", "37", "38", "39", "40", "41", "42", "43", "44"]:
        type = "Size"
        constant_rows.append({"id": len(constant_rows) + 1, "type": type, "value": value})
    for idx in [["2", "Style"], ["3", "Category"], ["9223372036854775802", "Material"]]:
        type = idx[1]
        child = json_data["Attribute"][idx[0]]["child"]
        for child_idx in child:
            value = child[child_idx]["name"].split("|")[0].strip()
            constant_rows.append({"id": len(constant_rows) + 1, "type": type, "value": value})

def get_constant_id(type: str, value: str, is_contains: bool = False, debug: bool = False) -> str:
    global constant_rows
    if debug:
        print(f"{type} - {value}")
    if not is_contains:
        for constant in constant_rows:
            if constant["type"] == type and constant["value"].replace("-", " ").lower() == value.lower():
                return constant["id"]
    else:
        for constant in constant_rows:
            if constant["type"] == type and value.lower() in constant["value"].replace("-", " ").lower():
                return constant["id"]
    return None

def get_products():
    global url, current_page, response, json_data, constant_rows, product_rows, product_detail_rows, product_image_rows, product_quantity_rows
    while(len(json_data["listProduct"])>0):
        print("Current page: ", current_page)
        for product in json_data["listProduct"]:
            try:
                # product detail page
                soup = BeautifulSoup((requests.get(product["linkBuyNow"])).content, "html.parser")

                name = product["productName"].split(" - ")[0].replace("'", "")
                quantity = product["statusQuantity"]
                images = [img["src"] for img in soup.find("div", class_="prd-detail-slide1").find_all("img")]
                price = int(product["productPrice"].split(" ")[0].replace(".", ""))
                origin_price = price - random.randrange(100000, 200000, 10000)
                gender = 2 if random.random() > 0.4 else random.randrange(0, 2, 1)
                gender_val = None
                if gender == 2:
                    gender_val = "UNISEX"
                elif gender == 1:
                    gender_val = "MALE"
                else:
                    gender_val = "FEMALE"

                description = {"images": [soup.find("div", class_="col-xs-12 col-sm-12 col-md-5 col-lg-5 prd-detail-right").find_all("img")[-1]["src"]]}

                # style id
                style_name = product["productName"].split(" - ")[1]
                style_id = get_constant_id("Style", style_name)

                # category id
                category_name = soup.find("ol", class_="breadcrumb").find_all("a")[1].text.strip()
                category_id = get_constant_id("Category", category_name)

                # material id
                material_name = None
                span = soup.find('span', attrs={'data-sheets-value': True})
                if span:
                    text = span.get_text(separator='').strip()
                    material_name = text.split('\n')[2].split(': ')[1]
                else:
                    divs = soup.find_all('div', class_='TypographyPresentation TypographyPresentation--m RichText3-paragraph--withVSpacingNormal RichText3-paragraph')
                    for div in divs:
                        if div.get_text(separator=' ').strip().startswith('Upper'):
                            material_name = div.get_text(separator=' ').strip().split(': ')[1]
                            break
                material_id = get_constant_id("Material", material_name.split("/")[-1].split(" ")[0] if "/" in material_name else material_name.split(" ")[0], is_contains=True)

                # color id
                color = [span["style"].split("#")[-1][:-1] for span in soup.find('li', class_="cb-color-fixed").find_all("span", class_="bg-color")][0]
                color_id = get_constant_id("Color", color, debug=False)
                if not color_id:
                    color_id = len(constant_rows) + 1
                    constant_rows.append({"id": color_id, "type": "Color", "value": color})

                if style_id is not None and category_id is not None and material_id is not None and color_id is not None:
                    # product rows
                    product_id = None
                    for row in product_rows:
                        if row["name"] == name:
                            product_id = row["id"]
                            break
                    if product_id is None:
                        product_id = len(product_rows) + 1
                        product_rows.append({"id": product_id, "name": name, "category_id": category_id, "material_id": material_id})

                    # product detail rows
                    product_detail_id = len(product_detail_rows) + 1
                    product_detail_rows.append({"id": product_detail_id, "origin_price": origin_price, "discount": 0, "price": price, "gender": gender_val, "description": json.dumps(description), "product_id": product_id, "color_id": color_id, "style_id": style_id})

                    # product image rows
                    for image in images:
                        product_image_rows.append({"id": len(product_image_rows) + 1, "product_detail_id": product_detail_id, "url": image})

                    # product quantity rows
                    size_ids = [row["id"] for row in constant_rows if row["type"] == "Size"]
                    for size_id in size_ids:
                        product_quantity_rows.append({"id": len(product_quantity_rows) + 1, "quantity": quantity, "product_detail_id": product_detail_id, "size_id": size_id})

            except Exception as e:
                print("Error: ", name, e)
                continue
        current_page += 1
        response = requests.get(f'{url}={current_page}')
        json_data = response.json()

file_path = "./database/initial_data.sql"

def clean_file(pattern: str):
    global file_path
    with open(file_path, 'r') as f:
        lines = f.readlines()

    for i, line in enumerate(lines):
        if line.strip().startswith(pattern):
            lines = lines[:i+1]
            with open(file_path, 'w') as f:
                f.writelines(lines)
            break

def write_sql_query_to_file(table: str, rows: list):
    with open(file_path, "a+") as f:
        print(f"Seeding {table} table...")
        f.write(f"\n\n-- {table} data\n")
        for row in rows:
            fields = ', '.join(row.keys())
            values = ''
            for idx, value in enumerate(row.values()):
                if idx < len(row.values()) - 1:
                    values += f"'{value}', " if isinstance(value, str) else f"{value}, "
                else:
                    values += f"'{value}'" if isinstance(value, str) else f"{value}"
            if fields and values:
                f.write(f"INSERT INTO {table} ({fields}) VALUES ({values});\n")

if __name__ == "__main__":
    get_constants()
    get_products()

    # seeding data
    clean_file(pattern="-- Data")
    write_sql_query_to_file("constants", constant_rows)
    write_sql_query_to_file("products", product_rows)
    write_sql_query_to_file("product_details", product_detail_rows)
    write_sql_query_to_file("product_images", product_image_rows)
    write_sql_query_to_file("product_quantities", product_quantity_rows)