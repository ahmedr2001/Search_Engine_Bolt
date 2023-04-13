import { useContext, useEffect } from "react";
import ChangeColors from "./Components/ChangeColors";
import Search from "./Components/Search";
import { applyTheme } from "./themes/utils";
import light from "./themes/light";
import dark from "./themes/dark";
function App() {
	useEffect(() => {
		applyTheme(dark);
	}, []);
	return (
		<div className={` mx-auto bg-primary w-full min-h-screen`}>
			<div className="w-4/5 mx-auto py-5 ">
				<ChangeColors />
				<div className="flex flex-col items-center py-32 gap-20">
					<h1 className={` text-6xl font-bold text-secondary`}>
						BOLT
					</h1>
					<Search />
				</div>
			</div>
		</div>
	);
}

export default App;
