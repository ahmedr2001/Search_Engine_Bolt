import { applyTheme } from "../themes/utils";
import dark from "../themes/dark";
import blue from "../themes/blue";
import light from "../themes/light";
import { IMappedTheme } from "../themes/utils";

export default function ChangeColors() {
	const themes = [dark, blue, light];
	return (
		<div className={`flex  gap-0 flex-row-reverse`}>
			{themes.map((item, i) => (
				<ChangeColorBtn key={i} theme={item} />
			))}
		</div>
	);
}

function ChangeColorBtn({ theme }: { theme: IMappedTheme }) {
	return (
		<div
			className={`
				w-5 h-5 rounded-full 
				ring-2
				ring-yellow-600
				cursor-pointer`}
			style={{
				backgroundColor: theme["--primary"]!,
			}}
			onClick={() => applyTheme(theme)}></div>
	);
}
