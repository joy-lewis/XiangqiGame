-- module (NICHT ÄNDERN!)
module XiangqiBot
    ( getMove
    , listMoves
    ) 
    where

import Data.Char
-- More modules may be imported

import Util

--- external signatures (NICHT ÄNDERN!)
getMove :: String -> String
getMove string = "a1-b1"

listMoves :: String -> String
listMoves string  = let split = splitOn ' ' string in
                    ['['] ++ listForm (listMovesOfColor (head split) (getColor (last split))) ++ [']']


listForm :: [a] -> [a]
listForm [x] = []
listForm (x:xs) = [x] ++ listForm xs


-- YOUR IMPLEMENTATION FOLLOWS HERE

{-
    for testing in ghci - try:  
    
    fieldPieceArrayToString (getAllFieldsOfColor "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR" Red)
        ^^ get all Pieces of 1 Color

    pieceToString (getPiece "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR" (Coord 0 0))
        ^^ get a single Piece on the coordinates

    redSoldierPossibleMoves "rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR" (Field (Coord 4 3) (Piece Soldier Red))
        ^^ returns the move string of the red soldier if movement is allowed 
        Expects the board as a string and the field the soldier is on 

-}

-- definiton of needed data types
data Color = Red | Black | White
data Coord = Coord{x :: Integer, y :: Integer}
data Field = Field{coord :: Coord, piece :: Piece}
data PieceType = General | Advisor | Elephant | Horse | Rook | Cannon | Soldier | Empty 
data Piece = Piece{pieceType :: PieceType, color :: Color} 
data Generals = Generals{redG :: Coord, blackG :: Coord}

coordTranslator:: Coord -> Coord
coordTranslator (Coord x 0) = (Coord x 9)
coordTranslator (Coord x 1) = (Coord x 8)
coordTranslator (Coord x 2) = (Coord x 7)
coordTranslator (Coord x 3) = (Coord x 6)
coordTranslator (Coord x 4) = (Coord x 5)
coordTranslator (Coord x 5) = (Coord x 4)
coordTranslator (Coord x 6) = (Coord x 3)
coordTranslator (Coord x 7) = (Coord x 2)
coordTranslator (Coord x 8) = (Coord x 1)
coordTranslator (Coord x 9) = (Coord x 0)
coordTranslator (Coord x y) = (Coord x y)

-- X = column | Y = row 
 
listMovesOfColor :: String -> Color -> String 
listMovesOfColor board color = listMovesOfColorHelper board (getAllFieldsOfColor board color) 
listMovesOfColorHelper:: String -> [Field] -> String  
listMovesOfColorHelper _ [] = ""
listMovesOfColorHelper board (x:xs) = getPossibleMovesOfField board x ++ listMovesOfColorHelper board xs  


getColor :: String -> Color
getColor string = if string == "r" then Red else Black


-- expects the Board as a string and 2 coord x y and returns whats on the field
getPiece :: String -> Coord -> Piece
getPiece board (Coord x y) = getPieceHelper board 0 0 (coordTranslator (Coord x y))
getPieceHelper :: String  -> Integer -> Integer -> Coord -> Piece
getPieceHelper (s:xs) x y (Coord tX tY) -- tX / column | tY / row --> target
    | x == tX && y == tY = if pieceTypeIsEmpty(checkPieceType s) then Piece Empty White 
                        else Piece (checkPieceType s) (if toUpper s == s then Red else Black) -- check if the target field is reached
    | not (isDigit s) && x <= 8 = getPieceHelper xs (x+1) y (Coord tX tY) -- check if the column is less then 8, if so add + 1
    | not (isDigit s) && x >= 9 = getPieceHelper xs 0 (y+1) (Coord tX tY) -- check if the end of column is reached, if so add + 1 to the row and set column to 0
    | isDigit s = if (toInteger (digitToInt s) +x) > tX && y == tY then Piece Empty White -- if its an digit, add the digit to the row and check if the targed field is in betwenn
                else getPieceHelper xs (toInteger (digitToInt s) + x) y (Coord tX tY) -- else add freeFields to the column
    | otherwise = Piece Empty White -- Exeption
getPieceHelper [] _ _ _ = Piece Empty White -- Exeption

-- expects a board as a string and a color and returns all pieces of the color on the board in an array 
-- that contains the x,y -- coordinates and the type of the piece
getAllFieldsOfColor :: String -> Color -> [Field]
getAllFieldsOfColor board color = getAllFieldsOfColorHelper board color 0 0
getAllFieldsOfColorHelper:: String -> Color -> Integer -> Integer -> [Field] 
getAllFieldsOfColorHelper board color x y 
    | y > 9 = [] 
    | x > 8 = getAllFieldsOfColorHelper board color 0 (y+1) 
    | otherwise = getAllFieldsOfColorHelper board color (x+1) y ++
        let (Piece pType pColor) = getPiece board (Coord x y) in
            [Field (Coord x y) (Piece pType pColor) | not (pieceTypeIsEmpty pType) && colorCheck pColor color]

pieceTypeIsEmpty :: PieceType -> Bool 
pieceTypeIsEmpty Empty = True 
pieceTypeIsEmpty _ = False

colorCheck :: Color -> Color -> Bool 
colorCheck Red Red = True 
colorCheck Black Black = True 
colorCheck White White = True 
colorCheck _ _  = False 

-- returns the type of a piece as a char
checkPieceType :: Char -> PieceType
checkPieceType char = checkPieceTypeHelper (toUpper char)
checkPieceTypeHelper 'G' = General
checkPieceTypeHelper 'A' = Advisor
checkPieceTypeHelper 'E' = Elephant
checkPieceTypeHelper 'H' = Horse
checkPieceTypeHelper 'R' = Rook
checkPieceTypeHelper 'C' = Cannon
checkPieceTypeHelper 'S' = Soldier
checkPieceTypeHelper _ = Empty


isCoordEmpty :: String -> Coord -> Bool 
isCoordEmpty board coord = pieceTypeIsEmpty (pieceType (getPiece board coord)) 

coordColor :: String -> Coord -> Color-> Bool
coordColor board coord = colorCheck (color (getPiece board coord))


-- matches the Piece on a field with pattern matching and calculates the possible moves an returns them in string format
getPossibleMovesOfField :: String -> Field -> String  
getPossibleMovesOfField board (Field coord (Piece Soldier Red))  = redSoldierPossibleMoves board (Field coord (Piece Soldier Red))
getPossibleMovesOfField board (Field coord (Piece Soldier Black)) = blackSoldierPossibleMoves board (Field coord (Piece Soldier Black))
getPossibleMovesOfField board (Field coord (Piece Advisor Red))  = redAdvPossibleMoves board (Field coord (Piece Advisor Red))
getPossibleMovesOfField board (Field coord (Piece Advisor Black))  = blackAdvPossibleMoves board (Field coord (Piece Advisor Black))
getPossibleMovesOfField board (Field coord (Piece General color)) = generalPossibleMoves board (Field coord (Piece General color))
getPossibleMovesOfField board (Field coord (Piece Horse color)) = horsePossibleMoves board (Field coord (Piece Horse color))
getPossibleMovesOfField board (Field coord (Piece Rook color)) = rookPossibleMoves board (Field coord (Piece Horse color))
getPossibleMovesOfField board (Field coord (Piece Cannon color)) = canonPossibleMoves board (Field coord (Piece Horse color))
getPossibleMovesOfField board (Field coord (Piece Elephant Red)) = redElephantPossibleMoves board (Field coord (Piece Elephant Red))
getPossibleMovesOfField board (Field coord (Piece Elephant Black)) = blackElephantPossibleMoves board (Field coord (Piece Elephant Black))
getPossibleMovesOfField _ _ = ""



--tGetPossibleMovesOfField board (Field coord (Piece Soldier Red))  = redSoldierPossibleMoves board (Field coord (Piece Soldier Red))
--tGetPossibleMovesOfField board (Field coord _)  = ""--redSoldierPossibleMoves board (Field coord (Piece Soldier Red))

coordOnBoard :: Coord -> Bool
coordOnBoard (Coord x y) = x>=0 && y>=0 && x<9 && y<=9

isGeneral :: PieceType -> Bool 
isGeneral General = True 
isGeneral _ = False 

-- check if coord is empty or the piece on coord has diffrent color then returns moveString or nothing if empty 
isCoordEmptyOrColorDiff :: String -> Field -> Coord -> String 
isCoordEmptyOrColorDiff board (Field coord (Piece _ color)) checkCoord =
        --let (Piece pieceType pieceColor) = getPiece board coord in
        if coordOnBoard checkCoord && (isCoordEmpty board checkCoord || not (coordColor board checkCoord color))
            then createMoveString coord checkCoord 
            else "" 
    -- second case soldier in enemy half 

-- the movement of red soldier
redSoldierPossibleMoves :: String -> Field -> String
redSoldierPossibleMoves board field = let (Field (Coord x y) _) = field  in  
    -- first case soldier in own half/ not crossed the river
    test board field (Coord x (y+1)) ++ 
    if y > 4 then test board field (Coord (x+1) y)
        ++ test board field (Coord (x-1) y)
        else ""  

blackSoldierPossibleMoves :: String -> Field -> String
blackSoldierPossibleMoves board field = let (Field (Coord x y) _) = field  in  
    -- first case soldier in own half/ not crossed the river
    test board field (Coord x (y-1)) ++ 
    if y < 5 then test board field (Coord (x+1) y)
        ++ test board field (Coord (x-1) y)
        else ""  

--- general
generalPossibleMoves :: String -> Field -> String
generalPossibleMoves board field = let (Field (Coord x y) _) = field in
            checkCoordForGeneral board field (Coord x y) (Coord (x+1) y)
            ++ checkCoordForGeneral board field (Coord x y) (Coord (x-1) y)
            ++ checkCoordForGeneral board field (Coord x y) (Coord x (y-1))
            ++ checkCoordForGeneral board field (Coord x y) (Coord x (y+1))

checkCoordForGeneral :: String -> Field -> Coord -> Coord -> String
checkCoordForGeneral board field (Coord x y) (Coord mX mY) = 
        if (mY >= 0 || mY <= 9) && (mY < 3 || mY > 6 ) && (mX > 2 && mX < 6) && not (verticalCheck board (Coord x y) (Coord mX mY))
            then isCoordEmptyOrColorDiff board field (Coord mX mY)
        else ""

-- start with Input 3 for x and y = 0 if u want the red general and y = 9 if u want the black general
findGeneral :: String -> Integer -> Integer -> Color -> Coord
findGeneral board x y color
    | x >= 3 && x <= 5 = if isGeneral(pieceType(getPiece board (Coord x y))) then (Coord x y)
                         else findGeneral board (x+1) y color
    | x == 6 =  if colorCheck color Red then findGeneral board 3 (y+1) color
                else findGeneral board 3 (y-1) color
    | otherwise = (Coord 0 0)

generalSameRow :: String -> Bool
generalSameRow board = let (Coord rX rY) = (findGeneral board 3 0 Red) in
                            let (Coord bX bY) = (findGeneral board 3 9 Black) in 
                                if rX == bX then True else False

verticalCheck :: String -> Coord -> Coord -> Bool
verticalCheck board (Coord x y) (Coord mX mY)= if coordColor board (Coord x y) Red then 
                                    let (Coord tX tY) = (findGeneral board 3 9 Black) in
                                        verticalCheckHelp board (Coord mX mY) (Coord tX tY)
                                  else let (Coord tX tY) = (findGeneral board 3 0 Red) in
                                        verticalCheckHelp board (Coord tX tY) (Coord mX mY)

verticalCheckHelp :: String -> Coord -> Coord -> Bool
verticalCheckHelp board (Coord rX rY) (Coord bX bY)
    | rX /= bX = False
    | rY == bY-1 = True
    | rY /= bY = if pieceTypeIsEmpty(pieceType(getPiece board (Coord rX (rY+1)))) then verticalCheckHelp board (Coord rX (rY+1)) (Coord bX bY)
                 else False
                       
flyingGeneralNonGenFig :: String -> Coord -> Bool
flyingGeneralNonGenFig board (Coord x y) = if generalSameRow board then
                                           let (Coord rX rY) = (findGeneral board 3 0 Red) in 
                                               let (Coord bX bY) = (findGeneral board 3 9 Black) in
                                                   verticalCheckNonGen board (Coord rX rY) (Coord bX bY) (Coord x y)
                                           else False

verticalCheckNonGen :: String -> Coord -> Coord -> Coord-> Bool
verticalCheckNonGen board (Coord rX rY) (Coord bX bY) (Coord fX fY)
    | rX /= bX = False
    | rY == fY-1 = verticalCheckNonGen board (Coord rX (rY+1)) (Coord bX bY) (Coord fX fY)
    | rY == bY-1 = True
    | rY /= bY = if pieceTypeIsEmpty(pieceType(getPiece board (Coord rX (rY+1)))) then verticalCheckNonGen board (Coord rX (rY+1)) (Coord bX bY) (Coord fX fY)
                 else False


test :: String -> Field -> Coord -> String 
test board (Field (Coord x y) (Piece _ color)) (Coord tX tY) =
        --let (Piece pieceType pieceColor) = getPiece board coord in
        if coordOnBoard (Coord tX tY) && (isCoordEmpty board (Coord tX tY) || not (coordColor board (Coord tX tY) color)) then
            if not (flyingGeneralNonGenFig board (Coord x y)) || (x == tX)
                then createMoveString (Coord x y) (Coord tX tY) 
                else "" 
        else ""

rookPossibleMoves :: String -> Field -> String
rookPossibleMoves board field = let (Field (Coord x y) _) = field in
                                    checkRookDirection board field 1 0
                                    ++ checkRookDirection board field (-1) 0
                                    ++ checkRookDirection board field 0 (-1)
                                    ++ checkRookDirection board field 0 1

checkRookDirection :: String -> Field -> Integer -> Integer -> String 
checkRookDirection board field x y = let (Field (Coord a b) _) = field in 
    test board field (Coord (x+a) (y+b)) ++ 
    if (x+a) < 9 && (x+a) >= 0 && (y+b) <= 9 && (y+b) >= 0 && isCoordEmpty board (Coord (a+x) (b+y)) 
         then
        if x > 0 
            then checkRookDirection board field (x+1) y
            else if x < 0 
                then checkRookDirection board field (x-1) y 
                else if y > 0 
                    then checkRookDirection board field x (y+1) 
                    else if y < 0
                        then checkRookDirection board field x (y-1) 
                        else "" 
        else ""    
  
-- movement canon
canonPossibleMoves :: String -> Field -> String
canonPossibleMoves board field = let (Field (Coord x y) _) = field in
                                    -- first case soldier in own half/ not crossed the river
                                    checkCanonDirection board field 1 0
                                    ++ checkCanonDirection board field (-1) 0
                                    ++ checkCanonDirection board field 0 (-1)
                                    ++ checkCanonDirection board field 0 1

checkCanonDirection :: String -> Field -> Integer -> Integer -> String 
checkCanonDirection board field x y = let (Field (Coord a b) (Piece _ color)) = field in 
    if isCoordEmpty board (Coord (x+a) (y+b)) then 
        test board field (Coord (x+a) (y+b)) ++ 
        if (x+a) < 9 && (x+a) >= 0 && (y+b) <= 9 && (y+b) >= 0
            then if x > 0
                then checkCanonDirection board field (x+1) y
                else if x < 0 
                    then checkCanonDirection board field (x-1) y 
                    else if y > 0 
                        then checkCanonDirection board field x (y+1) 
                        else if y < 0
                            then checkCanonDirection board field x (y-1) 
                            else "" 
        else ""   
    else if x > 0
        then checkCanonDirectionPhase2 board field (x+1) y
        else if x < 0 
            then checkCanonDirectionPhase2 board field (x-1) y 
            else  if y > 0 
                then checkCanonDirectionPhase2 board field x (y+1) 
                else if y < 0
                    then checkCanonDirectionPhase2 board field x (y-1) 
                    else "" 

checkCanonDirectionPhase2 :: String -> Field -> Integer -> Integer -> String 
checkCanonDirectionPhase2 board field x y = let (Field (Coord a b) (Piece _ color)) = field in 
    if not (coordColor board (Coord (a+x) (b+y)) color) then 
            if not (isCoordEmpty board (Coord (a+x) (b+y))) 
                then test board field (Coord (x+a) (y+b)) 
                else if (x+a) < 9 && (x+a) >= 0 && (y+b) <= 9 && (y+b) >= 0 && isCoordEmpty board (Coord (a+x) (b+y)) 
                        then if x > 0 
                            then checkCanonDirectionPhase2 board field (x+1) y
                            else if x < 0 
                                then checkCanonDirectionPhase2 board field (x-1) y 
                                else if y > 0 
                                    then checkCanonDirectionPhase2 board field x (y+1) 
                                    else if y < 0
                                        then checkCanonDirectionPhase2 board field x (y-1) 
                                        else "" 
                        else "" 
    else "" 

-- the movement of horse
horsePossibleMoves :: String -> Field -> String
horsePossibleMoves board (Field (Coord x y) piece) =  if not (flyingGeneralNonGenFig board (Coord x y)) then
                                                        lStep board 8 (Coord x y) (color (getPiece board ((Coord x y)))) ""
                                                      else ""

lStep :: String -> Int -> Coord -> Color -> String -> String
lStep board 0 (Coord x y) figColor moves = moves
-- first step bottom vertical
lStep board 1 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x) (y-1)) && not (coordColor board (Coord (x+1) (y-2)) figColor) 
                                            then moves ++ lStep board 0 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x+1) (y-2)))
                                        else moves ++ lStep board 0 (Coord x y) figColor ""

lStep board 2 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x) (y-1)) && not (coordColor board (Coord (x-1) (y-2)) figColor) 
                                            then moves ++ lStep board 1 (Coord x y) figColor(createMoveString (Coord x y) (Coord (x-1) (y-2)))
                                        else moves ++ lStep board 0 (Coord x y) figColor ""
-- first step top vertical 
lStep board 3 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x) (y+1)) && not (coordColor board (Coord (x-1) (y+2)) figColor) 
                                            then moves ++ lStep board 2 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x-1) (y+2)))
                                        else moves ++ lStep board 2 (Coord x y) figColor ""

lStep board 4 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x) (y+1)) && not (coordColor board (Coord (x+1) (y+2)) figColor) 
                                            then moves ++ lStep board 3 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x+1) (y+2)))
                                        else moves ++ lStep board 3 (Coord x y) figColor ""
-- first step right horizontal
lStep board 5 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x+1) (y)) && not (coordColor board (Coord (x+2) (y+1)) figColor) 
                                            then moves ++ lStep board 4 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x+2) (y+1)))
                                        else moves ++ lStep board 4 (Coord x y) figColor ""

lStep board 6 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x+1) (y)) && not (coordColor board (Coord (x+2) (y-1)) figColor) 
                                            then moves ++ lStep board 5 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x+2) (y-1)))
                                        else moves ++ lStep board 5 (Coord x y) figColor ""
-- first step left horizontal
lStep board 7 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x-1) (y)) && not (coordColor board (Coord (x-2) (y+1)) figColor) 
                                            then moves ++ lStep board 6 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x-2) (y+1)))
                                        else moves ++ lStep board 6 (Coord x y) figColor ""

lStep board 8 (Coord x y) figColor moves = if isCoordEmpty board (Coord (x-1) (y)) && not (coordColor board (Coord (x-2) (y-1)) figColor) 
                                            then moves ++ lStep board 7 (Coord x y) figColor (createMoveString (Coord x y) (Coord (x-2) (y-1)))
                                        else moves ++ lStep board 7 (Coord x y) figColor ""

-- the movement of red elephant
redElephantPossibleMoves :: String -> Field -> String
redElephantPossibleMoves board (Field (Coord x y) moves) = 
    if not (flyingGeneralNonGenFig board (Coord x y)) then
        if y+2 <= 4 then twoStepsDiagonalR board 4 (Coord x y) "" else twoStepsDiagonalR board 2 (Coord x y) ""
    else ""

twoStepsDiagonalR :: String -> Int -> Coord -> String -> String
twoStepsDiagonalR board 0 (Coord x y) moves = moves

twoStepsDiagonalR board 1 (Coord x y) moves = if isCoordEmpty board (Coord (x+1) (y-1))  &&  not (coordColor board (Coord (x+2) (y-2)) Red)
                                                    then moves ++ twoStepsDiagonalR board 0 (Coord x y) (createMoveString (Coord x y) (Coord (x+2) (y-2)))
                                                else moves ++ twoStepsDiagonalR board 0 (Coord x y) ""

twoStepsDiagonalR board 2 (Coord x y) moves = if isCoordEmpty board (Coord (x-1) (y-1)) && not (coordColor board (Coord (x-2) (y-2)) Red)
                                                    then moves ++ twoStepsDiagonalR board 1 (Coord x y) (createMoveString (Coord x y) (Coord (x-2) (y-2)))
                                                else moves ++ twoStepsDiagonalR board 1 (Coord x y) ""

twoStepsDiagonalR board 3 (Coord x y) moves = if isCoordEmpty board (Coord (x+1) (y+1)) && not (coordColor board (Coord (x+2) (y+2)) Red)
                                                    then moves ++ twoStepsDiagonalR board 2 (Coord x y) (createMoveString (Coord x y) (Coord (x+2) (y+2)))
                                                else moves ++ twoStepsDiagonalR board 2 (Coord x y) ""

twoStepsDiagonalR board 4 (Coord x y) moves = if isCoordEmpty board (Coord (x-1) (y+1)) && not (coordColor board (Coord (x-2) (y+2)) Red)
                                                    then moves ++ twoStepsDiagonalR board 3 (Coord x y) (createMoveString (Coord x y) (Coord (x-2) (y+2)))
                                                else moves ++ twoStepsDiagonalR board 3 (Coord x y) ""

-- the movement of black elephant
blackElephantPossibleMoves :: String -> Field -> String
blackElephantPossibleMoves board (Field (Coord x y) moves) = 
    if not (flyingGeneralNonGenFig board (Coord x y)) then
        if y-2 >= 5 then twoStepsDiagonalB board 4 (Coord x y) "" else twoStepsDiagonalB board 2 (Coord x y) ""
    else ""

twoStepsDiagonalB :: String -> Int -> Coord -> String -> String
twoStepsDiagonalB board 0 (Coord x y) moves = moves
twoStepsDiagonalB board 1 (Coord x y) moves = if isCoordEmpty board (Coord (x+1) (y+1)) && not (coordColor board (Coord (x+2) (y+2)) Black)
                                                    then moves ++ twoStepsDiagonalB board 0 (Coord x y) (createMoveString (Coord x y) (Coord (x+2) (y+2)))
                                                else moves ++ twoStepsDiagonalB board 0 (Coord x y) ""
 
twoStepsDiagonalB board 2 (Coord x y) moves = if isCoordEmpty board (Coord (x-1) (y+1)) && not (coordColor board (Coord (x-2) (y+2)) Black)
                                                    then moves ++ twoStepsDiagonalB board 1 (Coord x y) (createMoveString (Coord x y) (Coord (x-2) (y+2)))
                                                else moves ++ twoStepsDiagonalB board 1 (Coord x y) ""

twoStepsDiagonalB board 3 (Coord x y) moves = if isCoordEmpty board (Coord (x+1) (y-1)) && not (coordColor board (Coord (x+2) (y-2)) Black)
                                                    then moves ++ twoStepsDiagonalB board 2 (Coord x y) (createMoveString (Coord x y) (Coord (x+2) (y-2)))
                                                else moves ++ twoStepsDiagonalB board 2 (Coord x y) ""

twoStepsDiagonalB board 4 (Coord x y) moves = if isCoordEmpty board (Coord (x-1) (y-1)) && not (coordColor board (Coord (x-2) (y-2)) Black)
                                                    then moves ++ twoStepsDiagonalB board 3 (Coord x y) (createMoveString (Coord x y) (Coord (x-2) (y-2)))
                                                else moves ++ twoStepsDiagonalB board 3 (Coord x y) ""


-- the movement of red advisor
redAdvPossibleMoves :: String -> Field -> String
redAdvPossibleMoves board (Field (Coord x y) moves) = 
    if not (flyingGeneralNonGenFig board (Coord x y)) then
        if y+1 <= 2 then oneStepDiagonalR board 4 (Coord x y) "" else oneStepDiagonalR  board 2 (Coord x y) ""
    else ""

oneStepDiagonalR :: String -> Int -> Coord -> String -> String
oneStepDiagonalR board 0 (Coord x y) moves = moves
--bottom right
oneStepDiagonalR board 1 (Coord x y) moves = if not (coordColor board (Coord (x+1) (y-1)) Red) && x+1 <= 5
                                                then moves ++ oneStepDiagonalR board 0 (Coord x y) (createMoveString (Coord x y) (Coord (x+1) (y-1)))
                                            else moves ++ oneStepDiagonalR board 0 (Coord x y) ""
--bottom left
oneStepDiagonalR board 2 (Coord x y) moves = if not (coordColor board (Coord (x-1) (y-1)) Red) && x-1 >= 3
                                                then moves ++ oneStepDiagonalR board 1 (Coord x y) (createMoveString (Coord x y) (Coord (x-1) (y-1)))
                                            else moves ++ oneStepDiagonalR board 1 (Coord x y) ""
--top right
oneStepDiagonalR board 3 (Coord x y) moves = if not (coordColor board (Coord (x+1) (y+1)) Red) && x+1 <= 5
                                                then moves ++ oneStepDiagonalR board 2 (Coord x y) (createMoveString (Coord x y) (Coord (x+1) (y+1)))
                                            else moves ++ oneStepDiagonalR board 2 (Coord x y) ""
--top left
oneStepDiagonalR board 4 (Coord x y) moves = if not (coordColor board (Coord (x-1) (y+1)) Red) && x-1 >= 3
                                                then moves ++ oneStepDiagonalR board 3 (Coord x y) (createMoveString (Coord x y) (Coord (x-1) (y+1)))
                                            else moves ++ oneStepDiagonalR board 3 (Coord x y) ""

-- the movement of black advisor 
blackAdvPossibleMoves :: String -> Field -> String
blackAdvPossibleMoves board (Field (Coord x y) moves) = 
    if not (flyingGeneralNonGenFig board (Coord x y)) then
        if y-1 >= 7 then oneStepDiagonalB board 4 (Coord x y) "" else oneStepDiagonalB board 2 (Coord x y) ""
    else ""

oneStepDiagonalB :: String -> Int -> Coord -> String -> String
oneStepDiagonalB board 0 (Coord x y) moves = moves
--top right
oneStepDiagonalB board 1 (Coord x y) moves = if not (coordColor board (Coord (x+1) (y-1)) Black) && x+1 <= 5
                                                then moves ++ oneStepDiagonalB board 0 (Coord x y) (createMoveString (Coord x y) (Coord (x+1) (y+1)))
                                            else moves ++ oneStepDiagonalB board 0 (Coord x y) ""
--top left
oneStepDiagonalB board 2 (Coord x y) moves = if not (coordColor board (Coord (x-1) (y-1)) Black) && x-1 >= 3
                                                then moves ++ oneStepDiagonalB board 1 (Coord x y) (createMoveString (Coord x y) (Coord (x-1) (y+1)))
                                            else moves ++ oneStepDiagonalB board 1 (Coord x y) ""
--bottom right
oneStepDiagonalB board 3 (Coord x y) moves = if not (coordColor board (Coord (x+1) (y+1)) Black) && x+1 <= 5
                                                then moves ++ oneStepDiagonalB board 2 (Coord x y) (createMoveString (Coord x y) (Coord (x+1) (y-1)))
                                            else moves ++ oneStepDiagonalB board 2 (Coord x y) ""
--bottom left
oneStepDiagonalB board 4 (Coord x y) moves = if not (coordColor board (Coord (x-1) (y+1)) Black) && x-1 >= 3
                                                then moves ++ oneStepDiagonalB board 3 (Coord x y) (createMoveString (Coord x y) (Coord (x-1) (y-1)))
                                            else moves ++ oneStepDiagonalB board 3 (Coord x y) ""


-- expects 2 coord and returns them in expected move string format
createMoveString :: Coord -> Coord -> String 
createMoveString (Coord x y) (Coord a b) = 
    if (a >= 0 && a <= 8 && b >= 0 && b <= 9) then [xToChar x] ++ show y ++ "-" ++ [xToChar a] ++ show b ++ ","
    else ""


-- Translate X - Coord to char
xToChar :: Integer -> Char 
xToChar 0 = 'a' 
xToChar 1 = 'b' 
xToChar 2 = 'c' 
xToChar 3 = 'd' 
xToChar 4 = 'e' 
xToChar 5 = 'f' 
xToChar 6 = 'g' 
xToChar 7 = 'h' 
xToChar 8 = 'i' 


-- functions for testing or printing  

fieldPieceArrayToString :: [Field] -> String 
fieldPieceArrayToString ((Field (Coord x y) piece):xs) = pieceToString piece ++ "-" ++ "[" ++ show x ++ "," ++ show y ++ "] " ++ fieldPieceArrayToString xs
fieldPieceArrayToString [] = ""

pieceToString :: Piece -> String
--piieceToString (Piece Empty Red) = "empty Red" 
--pieceToString (Piece Empty White) = "empty White" 
pieceToString (Piece Empty _) = "empty" 
pieceToString (Piece General _) = "G" 
pieceToString (Piece Advisor _) = "A" 
pieceToString (Piece Elephant _) = "E" 
pieceToString (Piece Horse _) = "H" 
pieceToString (Piece Rook _) = "R" 
pieceToString (Piece Cannon _) = "C" 
pieceToString (Piece Soldier _) = "S" 



printCoord :: Coord -> String
printCoord (Coord x y) = show x ++ " " ++ show y


{- 
    we need: piece(type, color), board{row}, row{fields}
    return list with all pieces of color
        for every piece check possible moves
            first get possible (direction(fields)) on empty board then check if in every direction move would be possible (empty field or right color)
                ^^ minimize the driection as far as possible and merge all in the moves list
    Palast, Todesblick-General,            
    funcs:
        movesOnEmptyBoard:: Piece -> Color -> Field -> [Directions]
        movesOnBoard:: Piece -> Color -> [Direction] -> [MoveString]
        
-}

